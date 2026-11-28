package kh.edu.istad.platform.application.exception;

import kh.edu.istad.platform.domain.exception.DomainException;

public class BookNotFoundException extends DomainException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
