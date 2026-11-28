package kh.edu.istad.platform.domain.valueobject;

import kh.edu.istad.platform.domain.exception.DomainException;

import java.util.UUID;

public record LoanId(UUID id) {
    public LoanId{
        if (id == null) throw new DomainException("LoanId must not be null");
    }

    public static LoanId of(UUID id){
        return new LoanId(id);
    }

    public static LoanId generate(){
        return new LoanId(UUID.randomUUID());
    }
}
