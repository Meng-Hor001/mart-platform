package kh.edu.istad.platform.loan.domain.event;

import kh.edu.istad.platform.domain.event.DomainEvent;
import kh.edu.istad.platform.loan.domain.entity.Loan;

import java.time.ZonedDateTime;

public abstract class LoanEvent implements DomainEvent<Loan> {
    private final Loan loan;
    private final ZonedDateTime createdAt;

    protected LoanEvent(Loan loan, ZonedDateTime createdAt){
        this.loan = loan;
        this.createdAt = createdAt;
    }

    public Loan getLoan(){
        return loan;
    }
    public ZonedDateTime getCreatedAt (){
        return createdAt;
    }
}
