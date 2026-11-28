package kh.edu.istad.platform.loan.domain.exception;

import kh.edu.istad.platform.domain.exception.DomainException;

public class LoanDomainException extends DomainException {
    public LoanDomainException(String message){
        super(message);
    }

    public LoanDomainException(String message, Throwable cause){
        super(message, cause);
    }
}
