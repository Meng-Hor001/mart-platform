package kh.edu.istad.platform.application.exception;

import kh.edu.istad.platform.domain.exception.DomainException;

public class MemberNotFoundException extends DomainException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
