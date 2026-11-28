package kh.edu.istad.platform.customer.domain.exception;

import kh.edu.istad.platform.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {
    public CustomerDomainException(String message) {
        super(message);
    }

    public CustomerDomainException(String message, Throwable cause){
        super(message, cause);
    }
}
