package kh.edu.istad.platform.application.ports.out;

import kh.edu.istad.platform.domain.valueobject.MemberId;
import kh.edu.istad.platform.loan.domain.entity.Member;

import java.util.Optional;

public interface MemberPort {
    Optional<Member> findMember(MemberId memberId);
}
