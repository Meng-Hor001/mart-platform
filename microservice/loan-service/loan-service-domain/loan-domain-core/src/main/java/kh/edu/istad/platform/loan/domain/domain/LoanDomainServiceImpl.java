package kh.edu.istad.platform.loan.domain.domain;

import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.entity.Book;
import kh.edu.istad.platform.loan.domain.entity.Loan;
import kh.edu.istad.platform.loan.domain.entity.Member;
import kh.edu.istad.platform.loan.domain.event.LoanCreatedEvent;
import kh.edu.istad.platform.loan.domain.event.LoanExtendedEvent;
import kh.edu.istad.platform.loan.domain.event.LoanOverdueEvent;
import kh.edu.istad.platform.loan.domain.event.LoanReturnedEvent;
import kh.edu.istad.platform.loan.domain.valueobject.ReturnedCopy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;


public class LoanDomainServiceImpl implements LoanDomainService{
    private static final ZoneId UTC = ZoneId.of("UTC");
    private final LoanBorrowValidator borrowValidator = new LoanBorrowValidator();
    @Override
    public void validateAndInitiateLoan(Loan loan, Member member, List<Book> books, List<Loan> memberActiveLoans, LocalDate today, int loanDays) {
        List<Loan> activeLoans = memberActiveLoans == null ? List.of() : memberActiveLoans;

        loan.validateLoan();
        borrowValidator.validate(loan, member, books, activeLoans);
        loan.initializeLoan(today, loanDays);
    }

    @Override
    public LoanCreatedEvent activateLoan(Loan loan) {
        loan.activate();
        return new LoanCreatedEvent(loan, now());
    }

    @Override
    public void cancelLoan(Loan loan, List<String> failureMessages) {
        loan.cancel(failureMessages);
    }

    @Override
    public LoanExtendedEvent extendLoan(Loan loan, int days, LocalDate today) {
        loan.extend(days, today);
        return new LoanExtendedEvent(loan, now());
    }

    @Override
    public LoanOverdueEvent markOverdue(Loan loan, LocalDate today, Money dailyRate) {
        loan.markOverdue(today, dailyRate);
        return new LoanOverdueEvent(loan, loan.getFine(), now());
    }

    @Override
    public LoanReturnedEvent returnLoan(Loan loan, List<ReturnedCopy> returnedCopies, LocalDate today, Money dailyRate) {
        loan.returnBooks(returnedCopies,today, dailyRate);
        return new LoanReturnedEvent(loan, now());
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now(UTC);
    }
}
