package com.group.receiptapp.service.group;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.member.MemberBudgetResponse;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupReceiptSettingService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void toggleDuplicateCheck(Long groupId, boolean enable, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!member.isAdmin() || !group.getId().equals(member.getGroup().getId())) {
            throw new AccessDeniedException("해당 그룹의 관리자만 설정을 변경할 수 있습니다.");
        }

        group.setPreventDuplicateReceipt(enable);
        groupRepository.save(group);
    }

    // 관리자가 지출 한도 설정하기
    @Transactional
    public void updateMemberBudget(Long groupId, Long targetMemberId, BigDecimal newBudget, Long requesterId) {
        // 요청자(관리자) 조회 및 권한 확인
        Member admin = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보를 찾을 수 없습니다."));

        if (!admin.isAdmin() || !groupId.equals(admin.getGroup().getId())) {
            throw new AccessDeniedException("해당 그룹의 관리자만 멤버 지출 한도를 수정할 수 있습니다.");
        }

        // 대상 멤버 조회
        Member target = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 멤버를 찾을 수 없습니다."));

        if (!groupId.equals(target.getGroup().getId())) {
            throw new IllegalArgumentException("대상 멤버는 해당 그룹에 속하지 않습니다.");
        }

        // 예산 수정
        target.setBudget(newBudget);
        memberRepository.save(target);
    }

    // 그룹 멤버 한도 조회
    @Transactional(readOnly = true)
    public List<MemberBudgetResponse> getMembersWithBudget(Long groupId, Long requesterId) {
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보 없음"));

        if (!requester.isAdmin() || requester.getGroup() == null || !requester.getGroup().getId().equals(groupId)) {
            throw new AccessDeniedException("해당 그룹 관리자만 조회할 수 있습니다");
        }

        List<Member> members = memberRepository.findByGroupId(groupId);

        return members.stream()
                .filter(Member::isActive)
                .map(m -> new MemberBudgetResponse(
                        m.getId(),
                        m.getName(),
                        m.getEmail(),
                        m.getBudget()
                ))
                .collect(Collectors.toList());
    }

}
