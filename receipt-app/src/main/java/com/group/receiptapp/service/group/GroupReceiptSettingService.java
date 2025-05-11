package com.group.receiptapp.service.group;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
