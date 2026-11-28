package kh.edu.istad.platform.loan.domain.domain;

import kh.edu.istad.platform.loan.domain.entity.Book;
import kh.edu.istad.platform.loan.domain.entity.Member;
import kh.edu.istad.platform.loan.domain.event.LoanCreatedEvent;
import kh.edu.istad.platform.loan.domain.event.LoanExtendedEvent;
import kh.edu.istad.platform.loan.domain.event.LoanOverdueEvent;
import kh.edu.istad.platform.loan.domain.event.LoanReturnedEvent;
import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.entity.Loan;
import kh.edu.istad.platform.loan.domain.valueobject.ReturnedCopy;

import java.time.LocalDate;
import java.util.List;

public interface LoanDomainService {
    void validateAndInitiateLoan(Loan loan,
                                 Member member,
                                 List<Book> books,
                                 List<Loan> memberActiveLoans,
                                 LocalDate today,
                                 int loanDays);

    LoanCreatedEvent activateLoan(Loan loan);

    void cancelLoan(Loan loan, List<String> failureMessages);
    LoanExtendedEvent extendLoan(Loan loan, int days, LocalDate today);

    LoanOverdueEvent markOverdue(Loan loan, LocalDate today, Money dailyRate);

    LoanReturnedEvent returnLoan(Loan loan,
                                 List<ReturnedCopy> returnedCopies, LocalDate today, Money dailyRate);
}
