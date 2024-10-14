package com.group.receiptapp.service.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  // PasswordEncoder 의존성 추가

    /** @return null 로그인 실패 **/

    public Member login(String email, String rawPassword) {
        return memberRepository.findByEmail(email)
                .filter(member -> passwordEncoder.matches(rawPassword, member.getPassword()))  // 비밀번호 검증
                .orElse(null);
    }

}
