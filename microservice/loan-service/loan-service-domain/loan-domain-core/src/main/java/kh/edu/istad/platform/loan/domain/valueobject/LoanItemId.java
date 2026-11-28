package kh.edu.istad.platform.loan.domain.valueobject;

import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

public record LoanItemId(Long id) {
    public LoanItemId{
        if (id == null){
            throw new LoanDomainException("LoanItemId must not be null");
        }
    }

    public static LoanItemId of(Long id){
        return new LoanItemId(id);
    }
}
