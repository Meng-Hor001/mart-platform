package kh.edu.istad.platform.loan.domain.event;

import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.entity.Loan;

import java.time.ZonedDateTime;

public class LoanOverdueEvent extends LoanEvent {
    private final Money fine;
    public LoanOverdueEvent(Loan loan,Money fine, ZonedDateTime createdAt) {
        super(loan, createdAt);
        this.fine = fine;
    }

    public Money getFine(){
        return fine;
    }
}
