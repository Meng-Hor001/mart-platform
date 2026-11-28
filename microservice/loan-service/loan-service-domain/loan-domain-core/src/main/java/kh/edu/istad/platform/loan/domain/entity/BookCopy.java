package kh.edu.istad.platform.loan.domain.entity;

import kh.edu.istad.platform.domain.entity.BaseEntity;
import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

public class BookCopy extends BaseEntity<CopyId> {

    private final BookId bookId;
    private final String title;

    public BookCopy(CopyId copyId, BookId bookId, String title) {
        if (copyId == null) {
            throw new LoanDomainException("copyId must not be null");
        }
        if (bookId == null) {
            throw new LoanDomainException("bookId must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new LoanDomainException("title must not be blank");
        }
        super.setId(copyId);
        this.bookId = bookId;
        this.title = title.trim();
    }

    public BookId getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }
}
