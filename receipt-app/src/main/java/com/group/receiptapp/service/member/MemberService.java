package com.group.receiptapp.service.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.member.RefreshTokenRepository;
import com.group.receiptapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /* 회원가입 */
    @Transactional
    public Member join(Member member) {
        validateDuplicateEmail(member);  // 중복 이메일 검사
        String encodedPassword = passwordEncoder.encode(member.getPassword());  // 비밀번호 암호화
        member.setPassword(encodedPassword);  // 암호화된 비밀번호 설정
        return memberRepository.save(member);  // 회원 저장 후 Member 객체 반환
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

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 회원이 존재하지 않습니다: " + email));
    }

    // 멤버 필드 변경 사항 저장
    public void updateMember(Member member) {
        memberRepository.save(member);
    }

    // 이메일로 회원 조회 (로그인 시 사용)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void save(Member member) {
        memberRepository.save(member);  // 회원 정보 저장
    }

    // 회원 탈퇴
    public void deleteAccount(String token) {
        String email = jwtUtil.extractUsername(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (!member.isActive()) {
            throw new IllegalStateException("이미 탈퇴된 회원입니다.");
        }

        Group group = member.getGroup();

        if (member.isAdmin()) {
            if (group != null) {
                // 그룹 내 현재 관리자 외 다른 멤버가 있는지 확인
                List<Member> otherActiveMembers = group.getMembers().stream()
                        .filter(m -> !m.getId().equals(member.getId()) && m.isActive())
                        .toList();

                if (otherActiveMembers.isEmpty()) {
                    member.setGroup(null);         // 연결 해제
                    member.deactivate();           // isActive = false
                    memberRepository.save(member); // 변경 사항 저장

                    groupRepository.delete(group); // 그룹 삭제
                    refreshTokenRepository.deleteByMemberId(member.getId());
                    return;
                } else {
                    // 다른 멤버에게 권한 위임을 해야 함
                    throw new IllegalStateException("탈퇴하려면 먼저 다른 멤버에게 관리자 권한을 위임해야 합니다.");
                }
            }
        }

        // 일반 멤버 탈퇴 또는 관리자 권한 위임 후 탈퇴
        member.deactivate();
        member.setGroup(null);
        memberRepository.save(member);
        refreshTokenRepository.deleteByMemberId(member.getId());
    }

    // 비번 검증
    public boolean checkPassword(Member member, String rawPassword) {
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    // 비번 변경
    public void updatePassword(Member member, String newPassword) {
        String encoded = passwordEncoder.encode(newPassword);
        member.setPassword(encoded);
        memberRepository.save(member);
    }

    @Transactional
    public String leaveGroup(Member member) {
        Group group = member.getGroup();
        if (group == null) {
            throw new IllegalStateException("소속된 그룹이 없습니다.");
        }

        // 그룹 연결 해제
        member.setGroup(null);
        memberRepository.save(member);

        // 자신을 제외한 활성 멤버가 있는지 확인
        List<Member> remaining = group.getMembers().stream()
                .filter(Member::isActive)
                .filter(m -> !m.getId().equals(member.getId()))
                .toList();

        if (remaining.isEmpty()) {
            // 모든 멤버의 group 연결 끊기
            for (Member m : group.getMembers()) {
                m.setGroup(null); // 관계 끊기
                memberRepository.save(m); // 변경 사항 저장
            }

            // group 삭제
            groupRepository.delete(group);

            return "그룹 마지막 멤버 탈퇴, 그룹 삭제 완료";
        }

        return "그룹에서 성공적으로 탈퇴하였습니다.";
    }

}
