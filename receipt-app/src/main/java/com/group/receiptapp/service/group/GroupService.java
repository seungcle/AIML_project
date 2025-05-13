package com.group.receiptapp.service.group;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.join.JoinRequest;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.group.CreateGroupRequest;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.join.JoinRequestRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor

public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final JoinRequestRepository joinRequestRepository;

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class); // Logger 선언 및 초기화

    @PostConstruct
    @Transactional
    public void createDefaultGroup() {
        // 기본 그룹이 없을 경우에만 생성
        if (groupRepository.findByName("Default Group").isEmpty()) {
            Group defaultGroup = new Group("Default Group");
            groupRepository.save(defaultGroup);
        }
    }

    public Group createGroup(CreateGroupRequest request, Member admin) {
        // 그룹 이름 중복 확인
        groupRepository.findByName(request.getName()).ifPresent(existingGroup -> {
            throw new IllegalArgumentException("같은 이름의 그룹이 이미 존재합니다: " + request.getName());
        });

        // 중복이 없으면 그룹 생성
        Group group = new Group();
        group.setName(request.getName());
        group.setSpendingLimit(
                request.getSpendingLimit() != null
                        ? BigDecimal.valueOf(request.getSpendingLimit())
                        : BigDecimal.ZERO
        );  // 지출 한도 설정
        group.setPreventDuplicateReceipt(request.isPreventDuplicateReceipt());  // 중복 영수증 방지 설정
        groupRepository.save(group);

        // 관리자로 설정
        admin.setGroup(group);
        admin.setAdmin(true);
        memberRepository.save(admin);

        return group;
    }

    @Transactional
    public JoinRequest requestToJoinGroup(Long groupId, Member member) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

        if (joinRequestRepository.existsByMemberAndGroup(member, group)) {
            throw new IllegalArgumentException("이미 가입 신청한 그룹입니다.");
        }

        JoinRequest joinRequest = new JoinRequest(member, group);
        joinRequestRepository.save(joinRequest);

        return joinRequest;
    }

    public List<JoinRequest> findJoinRequestsByGroup(Long groupId) {
        logger.info("Fetching join requests for group ID: {}", groupId);
        List<JoinRequest> requests = joinRequestRepository.findByGroupIdAndStatus(groupId, JoinRequest.Status.PENDING);
        logger.info("Found {} join requests with PENDING status", requests.size());
        return requests;
    }

    // 가입 신청 상태 변경(가입 요청의 status를 APPROVED 또는 REJECTED로 업데이트)
    public void updateJoinRequestStatus(Long joinRequestId, JoinRequest.Status status) {
        JoinRequest joinRequest = joinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 요청을 찾을 수 없습니다."));

        joinRequest.setStatus(status);
        joinRequestRepository.save(joinRequest);
    }

    // 존재하는 가입 요청인지 조회하고 존재하면 이메일 리턴
    public String getMemberEmailByJoinRequestId(Long joinRequestId) {
        // JoinRequest 존재하지 않으면 예외 처리
        JoinRequest joinRequest = joinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 요청을 찾을 수 없습니다: ID=" + joinRequestId));

        // JoinRequest에서 Member 이메일 리턴
        return joinRequest.getMember().getEmail();
    }

    // 존재하는 가입 요청인지 조회하고 존재하면 가입요청 필드 리턴
    public JoinRequest getJoinRequestById(Long joinRequestId) {
        return joinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 요청을 찾을 수 없습니다."));
    }

    // 모든 그룹 조회
    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    // 그룹 ID로 특정 그룹 조회
    public Group findOne(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
    }

    // 그룹별 회원 조회 메서드
    public List<Member> findMembersByGroup(Long groupId) {
        Group group = findOne(groupId); // 그룹 조회
        return memberRepository.findByGroup(group); // 그룹 ID 대신 Group 객체로 조회
    }

    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
    }

    public Optional<Group> findByName(String name) {
        return groupRepository.findByName(name);
    }
}
