package kh.edu.istad.platform.domain.valueobject;

import kh.edu.istad.platform.domain.exception.DomainException;

import java.util.UUID;

public record MemberId(UUID id) {
    public MemberId{
        if (id == null){
            throw new DomainException("MemberId must not be null ");
        }
    }

    public static MemberId of(UUID id){
        return new MemberId(id);
    }
}
