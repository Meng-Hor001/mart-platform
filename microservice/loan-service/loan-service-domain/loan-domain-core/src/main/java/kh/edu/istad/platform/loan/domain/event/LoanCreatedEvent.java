package kh.edu.istad.platform.loan.domain.event;

import kh.edu.istad.platform.loan.domain.entity.Loan;

import java.time.ZonedDateTime;

public class LoanCreatedEvent extends LoanEvent{

    public LoanCreatedEvent(Loan loan, ZonedDateTime createdAt) {
        super(loan, createdAt);
    }
}
