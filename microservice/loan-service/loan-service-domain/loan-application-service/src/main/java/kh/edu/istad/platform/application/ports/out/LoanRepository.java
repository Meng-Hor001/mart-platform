package kh.edu.istad.platform.application.ports.out;

import kh.edu.istad.platform.domain.valueobject.LoanId;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.loan.domain.entity.Loan;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);

    Optional<Loan> findById(LoanId loanId);

    List<Loan> findActiveByMemberId(MemberId memberId);
}
