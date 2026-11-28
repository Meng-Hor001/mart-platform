package kh.edu.istad.platform.loan.domain.valueobject;

import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record LoanPeriod(LocalDate borrowedOn, LocalDate dueOn) {
    public static final int MAX_LOAN_DAYS = 30;

    public LoanPeriod {
        if (borrowedOn == null || dueOn == null) {
            throw new LoanDomainException("borrowedOn and dueOn must not be null");
        }
        if (!dueOn.isAfter(borrowedOn)) {
            throw new LoanDomainException("dueOn must be after borrowedOn");
        }
    }

    public static LoanPeriod start(LocalDate borrowedOn, int loanDays) {
        if (loanDays < 1 || loanDays > MAX_LOAN_DAYS) {
            throw new LoanDomainException("loanDays must be between 1 and " + MAX_LOAN_DAYS);
        }
        return new LoanPeriod(borrowedOn, borrowedOn.plusDays(loanDays));
    }

    public LoanPeriod extendBy(int days) {
        if (days <= 0) {
            throw new LoanDomainException("extension days must be greater than 0");
        }
        return new LoanPeriod(borrowedOn, dueOn.plusDays(days));
    }

    public boolean isOverdue(LocalDate today) {
        return today.isAfter(dueOn);
    }

    public int daysLate(LocalDate today) {
        if (!isOverdue(today)) {
            return 0;
        }
        return Math.toIntExact(ChronoUnit.DAYS.between(dueOn, today));
    }
}
