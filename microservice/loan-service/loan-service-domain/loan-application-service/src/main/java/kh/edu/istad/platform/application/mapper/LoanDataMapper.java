package kh.edu.istad.platform.application.mapper;

import kh.edu.istad.platform.application.dto.CreateLoanCommand;
import kh.edu.istad.platform.application.dto.CreateLoanResponse;
import kh.edu.istad.platform.domain.valueobject.BookId;
import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.loan.domain.entity.Book;
import kh.edu.istad.platform.loan.domain.entity.BookCopy;
import kh.edu.istad.platform.loan.domain.entity.Loan;
import kh.edu.istad.platform.loan.domain.entity.LoanItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LoanDataMapper {
    @Mapping(target = "loanId",     source = "loan.id.id")
    @Mapping(target = "loanStatus", source = "loan.status")
    @Mapping(target = "dueOn",      source = "loan.period.dueOn")
    @Mapping(target = "message",    source = "message")
    CreateLoanResponse loanToCreateLoanResponse(Loan loan, String message);

    default Loan createLoanCommandToLoan(CreateLoanCommand command, List<Book> books) {
        Map<BookId, Book> bookById = books.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity(), (first, second) -> first));

        List<LoanItem> items = command.items().stream()
                .map(item -> {
                    BookId bookId = BookId.of(item.bookId());
                    Book book = bookById.get(bookId);
                    BookCopy copy = new BookCopy(CopyId.of(item.copyId()), bookId, book.getTitle());
                    return new LoanItem(copy);
                })
                .toList();

        return Loan.builder()
                .memberId(MemberId.of(command.memberId()))
                .items(items)
                .build();
    }
}
