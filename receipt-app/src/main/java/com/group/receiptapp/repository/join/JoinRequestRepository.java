package com.group.receiptapp.repository.join;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.join.JoinRequest;
import com.group.receiptapp.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    boolean existsByMemberAndGroup(Member member, Group group);

    // 특정 그룹에 가입 신청한 사용자의 PENDING 상태 요청만 조회
    List<JoinRequest> findByGroupIdAndStatus(Long groupId, JoinRequest.Status status);

    // joinRequestId로 JoinRequest 찾기
    Optional<JoinRequest> findById(Long joinRequestId);
}