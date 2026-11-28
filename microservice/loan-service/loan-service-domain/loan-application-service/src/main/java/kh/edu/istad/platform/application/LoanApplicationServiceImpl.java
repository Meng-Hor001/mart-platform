package kh.edu.istad.platform.application;

import kh.edu.istad.platform.application.dto.CreateLoanCommand;
import kh.edu.istad.platform.application.dto.CreateLoanResponse;
import kh.edu.istad.platform.application.handler.LoanCreateCommandHandler;
import kh.edu.istad.platform.application.ports.in.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private final LoanCreateCommandHandler loanCreateCommandHandler;

    @Override
    public CreateLoanResponse createLoan(CreateLoanCommand command) {
        return loanCreateCommandHandler.createLoan(command);
    }
}
