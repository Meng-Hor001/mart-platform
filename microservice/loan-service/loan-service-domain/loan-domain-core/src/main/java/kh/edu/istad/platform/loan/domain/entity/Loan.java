package kh.edu.istad.platform.loan.domain.entity;

import kh.edu.istad.platform.domain.entity.AggregateRoot;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.LoanId;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;
import kh.edu.istad.platform.loan.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Loan extends AggregateRoot<LoanId> {
    private final MemberId memberId;
    private final List<LoanItem> items;
    private LoanPeriod period;
    private LoanStatus status;
    private Money fine;
    private int extensionCount;
    private List<String> failureMessages;


    //----------- Validate -------------//
    public void validateLoan(){
        validateInitiateLoan();
        validateItems();
    }

    private void validateInitiateLoan(){
        if (getId() != null || status != null){
            throw new LoanDomainException("Loan is not in correct state for initialization");
        }
        if (memberId == null){
            throw new LoanDomainException("memberId must not be null");
        }
        if (!fine.isZero()){
            throw new LoanDomainException("A new loan must start with zero fine");
        }
    }

    private void validateItems(){
        if (items.isEmpty()){
            throw new LoanDomainException("Loan must contain at least one item");
        }
        if (items.size() > 5){
            throw new LoanDomainException("Loan cannot contain more than 5 items");
        }
        Set<CopyId> seen = new HashSet<>();
        for (LoanItem item : items){
            CopyId copyId = item.getBookCopy().getId();
            if (!seen.add(copyId)){
                throw new LoanDomainException("Duplicate copy in loan: " + copyId.id());
            }
        }
    }

    // --------------- initialize ------------//
    public void initializeLoan(LocalDate today, int loanDays) {
        super.setId(LoanId.generate());
        this.period = LoanPeriod.start(today, loanDays);
        this.status = LoanStatus.REQUESTED;
        this.fine = Money.ZERO;
        this.extensionCount = 0;
        this.failureMessages = new ArrayList<>();
        initializeLoanItems();
    }

    private void initializeLoanItems() {
        long itemId = 1;
        for (LoanItem item : items) {
            item.initializeLoanItem(getId(), LoanItemId.of(itemId++));
        }
    }

    // --------- state transitions -----------//
    public void activate() {
        if (status != LoanStatus.REQUESTED) {
            throw new LoanDomainException("Loan is not in correct state for activate: " + status);
        }
        this.status = LoanStatus.ACTIVE;
    }

    // REQUESTED -> CANCELLED   (e.g. catalog PATCH returned 409)
    public void cancel(List<String> messages) {
        if (status != LoanStatus.REQUESTED) {
            throw new LoanDomainException("Loan is not in correct state for cancel: " + status);
        }
        this.status = LoanStatus.CANCELLED;
        addFailureMessages(messages);
    }

    public void extend(int days, LocalDate today) {
        if (!statusIs(LoanStatus.ACTIVE, LoanStatus.EXTENDED)) {
            throw new LoanDomainException("Loan is not in correct state for extend: " + status);
        }
        if (period.isOverdue(today)) {
            throw new LoanDomainException("Overdue loan cannot be extended");
        }
        if (extensionCount >= 2) {
            throw new LoanDomainException("Loan can be extended at most 2 times");
        }
        if (days < 1 || days > 14) {
            throw new LoanDomainException("Extension days must be between 1 and 14");
        }
        this.period = period.extendBy(days);
        this.extensionCount++;
        this.status = LoanStatus.EXTENDED;
    }

    // ACTIVE | EXTENDED -> OVERDUE   (called by a daily scheduler)
    public void markOverdue(LocalDate today, Money dailyRate) {
        if (!statusIs(LoanStatus.ACTIVE, LoanStatus.EXTENDED)) {
            throw new LoanDomainException("Loan is not in correct state for markOverdue: " + status);
        }
        if (!period.isOverdue(today)) {
            throw new LoanDomainException("Loan is not overdue yet, due on " + period.dueOn());
        }
        this.fine = lateFine(today, dailyRate);
        this.status = LoanStatus.OVERDUE;
    }

    public void returnBooks(List<ReturnedCopy> returnedCopies, LocalDate today, Money dailyRate) {
        if (!statusIs(LoanStatus.ACTIVE, LoanStatus.EXTENDED, LoanStatus.OVERDUE)) {
            throw new LoanDomainException("Loan is not in correct state for returnBooks: " + status);
        }
        validateReturnedCopies(returnedCopies);

        // all checks passed -> now change state
        Money total = lateFine(today, dailyRate);
        for (ReturnedCopy returned : returnedCopies) {
            LoanItem item = findItem(returned.copyId());
            item.markReturned(returned.condition(), returned.damagePhotoUrl());
            total = total.add(conditionFine(returned.condition(), returned.replacementCost()));
        }
        this.fine = total;
        this.status = LoanStatus.RETURNED;
    }

    private void validateReturnedCopies(List<ReturnedCopy> returnedCopies) {
        if (returnedCopies == null || returnedCopies.isEmpty()) {
            throw new LoanDomainException("Returned copies must not be empty");
        }
        if (returnedCopies.size() != items.size()) {
            throw new LoanDomainException("All " + items.size() + " copies must be returned together");
        }
        Set<CopyId> seen = new HashSet<>();
        for (ReturnedCopy returned : returnedCopies) {
            if (!seen.add(returned.copyId())) {
                throw new LoanDomainException("Copy returned twice: " + returned.copyId().id());
            }
            findItem(returned.copyId());   // throws if the copy is not in THIS loan
        }
    }

    private LoanItem findItem(CopyId copyId) {
        return items.stream()
                .filter(item -> item.getBookCopy().getId().equals(copyId))
                .findFirst()
                .orElseThrow(() -> new LoanDomainException(
                        "Copy " + copyId.id() + " does not belong to loan " + getId().id()));
    }

    private Money lateFine(LocalDate today, Money dailyRate) {
        if (dailyRate == null) {
            throw new LoanDomainException("dailyRate must not be null");
        }
        return dailyRate.multiply(period.daysLate(today));
    }

    private Money conditionFine(BookCondition condition, Money replacementCost) {
        return switch (condition) {
            case GOOD    -> Money.ZERO;
            case DAMAGED -> replacementCost.multiply(new BigDecimal("0.30"));   // 30% of cost
            case LOST    -> replacementCost;                                    // full cost
        };
    }

    private boolean statusIs(LoanStatus... allowed) {
        for (LoanStatus status : allowed) {
            if (status == status) {
                return true;
            }
        }
        return false;
    }

    private void addFailureMessages(List<String> messages) {
        if (messages == null) {
            return;
        }
        messages.stream()
                .filter(message -> message != null && !message.isBlank())
                .forEach(failureMessages::add);
    }


    private Loan(Builder builder) {
        super.setId(builder.id);
        memberId = builder.memberId;
        items = builder.items;
        period = builder.period;
        status = builder.status;
        fine = builder.fine;
        extensionCount = builder.extensionCount;
        failureMessages = builder.failureMessages;
    }


    public static final class Builder {
        private LoanId id;
        private MemberId memberId;
        private List<LoanItem> items;
        private LoanPeriod period;
        private LoanStatus status;
        private Money fine;
        private int extensionCount;
        private List<String> failureMessages;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(LoanId val) {
            id = val;
            return this;
        }

        public Builder memberId(MemberId val) {
            memberId = val;
            return this;
        }

        public Builder items(List<LoanItem> val) {
            items = val;
            return this;
        }

        public Builder period(LoanPeriod val) {
            period = val;
            return this;
        }

        public Builder status(LoanStatus val) {
            status = val;
            return this;
        }

        public Builder fine(Money val) {
            fine = val;
            return this;
        }

        public Builder extensionCount(int val) {
            extensionCount = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Loan build() {
            return new Loan(this);
        }
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public List<LoanItem> getItems() {
        return items;
    }

    public LoanPeriod getPeriod() {
        return period;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Money getFine() {
        return fine;
    }

    public int getExtensionCount() {
        return extensionCount;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
