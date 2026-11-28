package kh.edu.istad.platform.application.handler;

import kh.edu.istad.platform.application.dto.CreateLoanCommand;
import kh.edu.istad.platform.application.dto.CreateLoanResponse;
import kh.edu.istad.platform.application.exception.BookNotFoundException;
import kh.edu.istad.platform.application.exception.MemberNotFoundException;
import kh.edu.istad.platform.application.mapper.LoanDataMapper;
import kh.edu.istad.platform.application.ports.out.CatalogPort;
import kh.edu.istad.platform.application.ports.out.LoanRepository;
import kh.edu.istad.platform.application.ports.out.MemberPort;
import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.loan.domain.domain.LoanDomainService;
import kh.edu.istad.platform.loan.domain.entity.Book;
import kh.edu.istad.platform.loan.domain.entity.Loan;
import kh.edu.istad.platform.loan.domain.entity.Member;
import kh.edu.istad.platform.loan.domain.event.LoanCreatedEvent;
import kh.edu.istad.platform.loan.domain.exception.CopyNotAvailableException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LoanCreateCommandHandler {
    private final LoanDomainService loanDomainService;
    private final LoanRepository loanRepository;
    private final MemberPort memberPort;
    private final CatalogPort catalogPort;
    private final LoanDataMapper loanDataMapper;

    public LoanCreateCommandHandler(LoanDomainService loanDomainService,
                                    LoanRepository loanRepository,
                                    MemberPort memberPort,
                                    CatalogPort catalogPort,
                                    LoanDataMapper loanDataMapper) {
        this.loanDomainService = loanDomainService;
        this.loanRepository = loanRepository;
        this.memberPort = memberPort;
        this.catalogPort = catalogPort;
        this.loanDataMapper = loanDataMapper;
    }

    @Transactional
    public CreateLoanResponse createLoan(CreateLoanCommand command) {
        // 1. fetch remote data
        MemberId memberId = MemberId.of(command.memberId());
        Member member = memberPort.findMember(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member " + command.memberId() + " not found"));

        List<BookId> bookIds = command.items().stream()
                .map(item -> BookId.of(item.bookId()))
                .distinct()
                .toList();
        List<Book> books = catalogPort.findAvailableBooks(bookIds);
        checkAllBooksFound(bookIds, books);

        // 2. fetch local data
        List<Loan> activeLoans = loanRepository.findActiveByMemberId(memberId);

        // 3. domain: validate (L3 + L4) and initialize -> REQUESTED
        Loan loan = loanDataMapper.createLoanCommandToLoan(command, books);
        int loanDays = command.loanDays() == null ? 14 : command.loanDays();
        loanDomainService.validateAndInitiateLoan(loan, member, books, activeLoans, LocalDate.now(), loanDays);
        loanRepository.save(loan);

        // 4. reserve copies in catalog
        List<CopyId> copyIds = loan.getItems().stream()
                .map(item -> item.getBookCopy().getId())
                .toList();
        try {
            catalogPort.markCopiesBorrowed(copyIds);
        } catch (CopyNotAvailableException e) {
            loanDomainService.cancelLoan(loan, List.of(e.getMessage()));
            loanRepository.save(loan);
            return loanDataMapper.loanToCreateLoanResponse(loan, e.getMessage());
        }

        // 5. REQUESTED -> ACTIVE
        LoanCreatedEvent event = loanDomainService.activateLoan(loan);
        loanRepository.save(loan);
        // TODO later: publish event to Kafka

        return loanDataMapper.loanToCreateLoanResponse(loan, "Loan created successfully");
    }

    private void checkAllBooksFound(List<BookId> requested, List<Book> found) {
        Set<BookId> foundIds = found.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        List<BookId> missing = requested.stream()
                .filter(bookId -> !foundIds.contains(bookId))
                .toList();
        if (!missing.isEmpty()) {
            throw new BookNotFoundException("Books not found or not available: "
                    + missing.stream().map(BookId::id).toList());
        }
    }
}
