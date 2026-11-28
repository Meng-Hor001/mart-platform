package kh.edu.istad.platform.domain.valueobject;

import kh.edu.istad.platform.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null) {
            throw new DomainException("Money amount must not be null");
        }
        if (amount.signum() < 0) {
            throw new DomainException("Money amount must not be negative");
        }
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public boolean isZero(){
        return amount.signum() == 0;
    }

    public boolean isGreaterThanZero() {
        return amount.signum() > 0;
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money multiply(int times) {
        return new Money(amount.multiply(BigDecimal.valueOf(times)));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor));
    }
}

