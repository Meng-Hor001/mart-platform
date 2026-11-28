package kh.edu.istad.platform.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record CreateLoanCommand(
        @NotNull
        UUID memberId,
        @NotEmpty
        @Size(max = 5)
        List<@Valid @NotNull CreateLoanItemCommand> items,
        @Min(1)
        @Max(30)
        Integer loanDays
) {
}
