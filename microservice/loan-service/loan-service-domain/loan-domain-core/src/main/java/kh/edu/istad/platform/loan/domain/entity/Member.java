package kh.edu.istad.platform.loan.domain.entity;

import kh.edu.istad.platform.domain.entity.AggregateRoot;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

public class Member extends AggregateRoot<MemberId> {
    private final boolean active;
    private final int borrowLimit;
    private final Money unpaidFines;

    public Member(MemberId memberId, boolean active, int borrowLimit,  Money unpaidFines) {
        if (memberId == null){
            throw new LoanDomainException("memberId must not be null");
        }
        if (borrowLimit < 0){
            throw new LoanDomainException("borrowLimit must not be negative");
        }
        if (unpaidFines == null){
            throw new LoanDomainException("unpaidFines must not be null");
        }
        super.setId(memberId);
        this.active = active;
        this.borrowLimit = borrowLimit;
        this.unpaidFines = unpaidFines;
    }
    public boolean isActive(){
        return active;
    }
    public int getBorrowLimit(){
        return borrowLimit;
    }
    public Money getUnpaidFines(){
        return unpaidFines;
    }
}
