package kh.edu.istad.platform.domain.valueobject;

import kh.edu.istad.platform.domain.exception.DomainException;

import java.util.UUID;

public record CopyId(UUID id) {
    public CopyId{
        if (id == null) throw new DomainException("CopyId must not be null");
    }
    public static CopyId of(UUID id){
        return new CopyId(id);
    }
}
