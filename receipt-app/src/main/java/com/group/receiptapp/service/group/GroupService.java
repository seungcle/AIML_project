package com.group.receiptapp.service.group;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class GroupService {
    private final GroupRepository groupRepository;
    private  final MemberRepository memberRepository;

    @PostConstruct
    @Transactional
    public void createDefaultGroup() {
        // 기본 그룹이 없을 경우에만 생성
        if (groupRepository.findByName("Default Group").isEmpty()) {
            Group defaultGroup = new Group("Default Group");
            groupRepository.save(defaultGroup);
        }
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
        return memberRepository.findByGroupId(groupId);
    }

    public Optional<Group> findByName(String name) {
        return groupRepository.findByName(name);
    }
}
