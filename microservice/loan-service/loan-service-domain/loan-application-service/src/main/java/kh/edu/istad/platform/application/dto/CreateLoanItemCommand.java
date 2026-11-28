package kh.edu.istad.platform.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLoanItemCommand(
        @NotNull
        UUID bookId,
        @NotNull
        UUID copyId
) {
}
