package kh.edu.istad.platform.loan.domain.entity;

import kh.edu.istad.platform.domain.entity.AggregateRoot;
import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

import java.util.List;



public class Book extends AggregateRoot<BookId> {
    private final String title;
    private final List<CopyId> availableCopies;
    private final Money replacementCost;

    public Book(BookId bookId, String title, List<CopyId> availableCopies, Money replacementCost) {
        if (bookId == null) {
            throw new LoanDomainException("bookId must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new LoanDomainException("title must not be blank");
        }
        if (replacementCost == null) {
            throw new LoanDomainException("replacementCost must not be null");
        }
        super.setId(bookId);
        this.title = title.trim();
        this.availableCopies = availableCopies == null ? List.of() : List.copyOf(availableCopies);
        this.replacementCost = replacementCost;
    }

    public boolean isAvailable(CopyId copyId) {
        return availableCopies.contains(copyId);
    }
    public String getTitle() {
        return title;
    }
    public List<CopyId> getAvailableCopies(){
        return availableCopies;
    }
    public Money getReplacementCost(){
        return replacementCost;
    }
}
