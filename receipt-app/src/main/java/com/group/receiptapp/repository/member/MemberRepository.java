package com.group.receiptapp.repository.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 회원 이름으로 중복 체크
    List<Member> findByName(String name);

    // 이메일로 회원 조회 (로그인에 사용할 수 있음)
    Optional<Member> findByEmail(String email);

    // 그룹에 속한 회원들을 조회하는 메서드
    List<Member> findByGroupId(Long groupId);

    List<Member> findByGroup(Group group);

    // 탈퇴하지 않은 회원 조회
    List<Member> findAllByIsActiveTrue();
}
