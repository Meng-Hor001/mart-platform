package kh.edu.istad.platform.customer.domain.valueobject;

import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;

public record CustomerName(
        String firstName,
        String lastName
) {
    private static final int MAX_LENGTH = 50;

    public CustomerName {
        firstName = validate(firstName, "firstName");
        lastName  = validate(lastName, "lastName");
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    private static String validate(String value, String field) {
        if (value == null || value.isBlank())
            throw new CustomerDomainException(field + " must not be blank");
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH)
            throw new CustomerDomainException(field + " must be at most " + MAX_LENGTH + " characters");
        return trimmed;
    }
}

