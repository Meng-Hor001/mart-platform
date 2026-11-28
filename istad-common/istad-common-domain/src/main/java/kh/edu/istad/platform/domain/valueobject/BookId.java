package kh.edu.istad.platform.domain.valueobject;

import kh.edu.istad.platform.domain.exception.DomainException;

import java.util.UUID;

public record BookId(UUID id) {
    public BookId{
        if (id == null) throw new DomainException("BookId must not be null");
    }
    public static BookId of(UUID id){
        return new BookId(id);
    }
}
