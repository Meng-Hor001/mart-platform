package kh.edu.istad.platform.loan.domain.domain;

import kh.edu.istad.platform.loan.domain.entity.Book;
import kh.edu.istad.platform.loan.domain.entity.Member;
import kh.edu.istad.platform.loan.domain.exception.CopyNotAvailableException;
import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.loan.domain.entity.BookCopy;
import kh.edu.istad.platform.loan.domain.entity.Loan;
import kh.edu.istad.platform.loan.domain.entity.LoanItem;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LoanBorrowValidator {
    void validate(Loan loan, Member member, List<Book> books, List<Loan> activeLoans) {
        validateMember(loan, member, activeLoans);
        validateBooks(loan, books);
        validateNoSameBookOnActiveLoan(loan, activeLoans);
    }

    private void validateMember(Loan loan, Member member, List<Loan> activeLoans) {
        if (member == null) {
            throw new LoanDomainException("member must not be null");
        }
        if (!member.getId().equals(loan.getMemberId())) {
            throw new LoanDomainException("Member does not match the loan");
        }
        if (!member.isActive()) {
            throw new LoanDomainException("Member " + member.getId().id() + " is not active");
        }
        if (!member.getUnpaidFines().isZero()) {
            throw new LoanDomainException("Member has unpaid fines: " + member.getUnpaidFines().amount());
        }
        int alreadyBorrowed = activeLoans.stream()
                .mapToInt(activeLoan -> activeLoan.getItems().size())
                .sum();
        if (alreadyBorrowed + loan.getItems().size() > member.getBorrowLimit()) {
            throw new LoanDomainException("Borrow limit exceeded: limit " + member.getBorrowLimit()
                    + ", already borrowed " + alreadyBorrowed
                    + ", requested " + loan.getItems().size());
        }
    }

    private void validateBooks(Loan loan, List<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new LoanDomainException("books must not be empty");
        }
        Map<BookId, Book> bookById = books.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity(), (first, second) -> first));

        for (LoanItem item : loan.getItems()) {
            BookCopy copy = item.getBookCopy();
            Book book = bookById.get(copy.getBookId());
            if (book == null) {
                throw new LoanDomainException("Book " + copy.getBookId().id() + " not found");
            }
            if (!book.isAvailable(copy.getId())) {
                throw new CopyNotAvailableException("Copy " + copy.getId().id() + " is not available");
            }
        }
    }

    private void validateNoSameBookOnActiveLoan(Loan loan, List<Loan> activeLoans) {
        Set<BookId> booksOnLoan = activeLoans.stream()
                .flatMap(activeLoan -> activeLoan.getItems().stream())
                .map(item -> item.getBookCopy().getBookId())
                .collect(Collectors.toSet());

        for (LoanItem item : loan.getItems()) {
            BookId bookId = item.getBookCopy().getBookId();
            if (booksOnLoan.contains(bookId)) {
                throw new LoanDomainException("Member already has book " + bookId.id() + " on an active loan");
            }
        }
    }
}
