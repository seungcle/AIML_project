package com.group.receiptapp.service.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    public Long join(Member member) {
        validateDuplicateEmail(member);  // 중복 이메일 검사
        String encodedPassword = passwordEncoder.encode(member.getPassword());  // 비밀번호 암호화
        member.setPassword(encodedPassword);  // 암호화된 비밀번호 설정
        memberRepository.save(member);
        return member.getId();
    }

    // 이메일 중복 검사
    private void validateDuplicateEmail(Member member) {
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    public List<Member> findMember() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElse(null); // findById로 수정
    }

    public List<Member> findMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
        return group.getMembers(); // 그룹에 속한 회원들을 반환
    }

    // 이메일로 회원 조회 (로그인 시 사용)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void save(Member member) {
        memberRepository.save(member);  // 회원 정보 저장
    }

}
