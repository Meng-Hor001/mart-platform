package kh.edu.istad.platform.loan.domain.valueobject;

import kh.edu.istad.platform.domain.valueobject.CopyId;
import kh.edu.istad.platform.domain.valueobject.Money;
import kh.edu.istad.platform.loan.domain.exception.LoanDomainException;

public record ReturnedCopy(
        CopyId copyId,
        BookCondition condition,
        String damagePhotoUrl,
        Money replacementCost
) {
    public ReturnedCopy{
        if (copyId == null){
            throw new LoanDomainException("CopyId must not be null");
        }
        if (condition == null){
            throw new LoanDomainException("Condition must not be null");
        }
        if (replacementCost == null){
            throw new LoanDomainException("replacementcost must not be null");
        }
    }
}
