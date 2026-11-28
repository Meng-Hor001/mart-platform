package kh.edu.istad.platform.application.ports.in;


import jakarta.validation.Valid;
import kh.edu.istad.platform.application.dto.CreateLoanCommand;
import kh.edu.istad.platform.application.dto.CreateLoanResponse;

public interface LoanApplicationService{

    CreateLoanResponse createLoan(@Valid CreateLoanCommand command
    );
}
