package kh.edu.istad.platform.application.dto;

import kh.edu.istad.platform.loan.domain.valueobject.LoanStatus;

import java.time.LocalDate;
import java.util.UUID;

public record CreateLoanResponse(
        UUID loanId,
        LoanStatus loanStatus,
        LocalDate dueOn,
        String message
) {
}
