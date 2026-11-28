package kh.edu.istad.platform.application.ports.out;

import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.loan.domain.entity.Book;

import java.util.List;

public interface CatalogPort {
    List<Book> findAvailableBooks(List<BookId> bookIds);

    void markCopiesBorrowed(List<CopyId> copyIds);
}
