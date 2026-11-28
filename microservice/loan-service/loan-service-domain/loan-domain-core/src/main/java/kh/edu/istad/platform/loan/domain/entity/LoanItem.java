package kh.edu.istad.platform.loan.domain.entity;

import kh.edu.istad.platform.domain.entity.BaseEntity;
import kh.edu.istad.platform.domain.valueobject.LoanId;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;
import kh.edu.istad.platform.loan.domain.valueobject.BookCondition;
import kh.edu.istad.platform.loan.domain.valueobject.LoanItemId;

public class LoanItem extends BaseEntity<LoanItemId> {
    private LoanId loanId;
    private final BookCopy bookCopy;
    private BookCondition condition;
    private String damagePhotoUrl;

    public LoanItem(BookCopy bookCopy) {
        if (bookCopy == null) {
            throw new LoanDomainException("bookCopy must not be null");
        }
        this.bookCopy = bookCopy;
    }

    void initializeLoanItem(LoanId loanId, LoanItemId loanItemId) {
        this.loanId = loanId;
        super.setId(loanItemId);
    }

    void markReturned(BookCondition condition, String damagePhotoUrl) {
        if (isReturned()) {
            throw new LoanDomainException(
                    "Copy " + bookCopy.getId().id() + " is already returned");
        }
        if (condition == null) {
            throw new LoanDomainException("condition must not be null");
        }
        if (condition == BookCondition.DAMAGED
                && (damagePhotoUrl == null || damagePhotoUrl.isBlank())) {
            throw new LoanDomainException("damagePhotoUrl is required when condition is DAMAGED");
        }
        this.condition = condition;
        this.damagePhotoUrl = damagePhotoUrl;
    }

    public boolean isReturned() {
        return condition != null;
    }

    public LoanId getLoanId(){
        return loanId;
    }
    public BookCopy getBookCopy(){
        return bookCopy;
    }
    public BookCondition getCondition(){
        return condition;
    }
    public String getDamagePhotoUrl(){
        return damagePhotoUrl;
    }
}
