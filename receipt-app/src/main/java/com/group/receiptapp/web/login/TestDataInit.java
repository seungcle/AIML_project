package com.group.receiptapp.web.login;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.service.group.GroupService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        Group defaultGroup = groupService.findByName("Default Group")
                .orElseThrow(() -> new IllegalStateException("기본 그룹이 없습니다."));

        Member member = new Member();
        member.setEmail("test@test.com");
        member.setPassword(passwordEncoder.encode("test123!"));  // 비밀번호 암호화
        member.setName("테스터");
        member.setGroup(defaultGroup);
        memberRepository.save(member);
    }
}

