package com.group.receiptapp.service.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.token.RefreshToken;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.member.RefreshTokenRepository;
import com.group.receiptapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  // PasswordEncoder 의존성 추가
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil; // JwtUtil 주입

    /** @return null 로그인 실패 **/

    public Member login(String email, String rawPassword) {
        return memberRepository.findByEmail(email)
                .filter(member -> passwordEncoder.matches(rawPassword, member.getPassword()))  // 비밀번호 검증
                .orElse(null);
    }

    // 리프레시 토큰 발급
    public String generateRefreshToken(Member member) {
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail()); // JWT 유틸 클래스를 통해 리프레시 토큰 생성
        RefreshToken tokenEntity = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(tokenEntity); // DB에 저장
        return refreshToken;
    }

    public void invalidateRefreshToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken); // 토큰 검색
        if (token.isPresent()) {
            refreshTokenRepository.delete(token.get()); // 해당 토큰 삭제
        } else {
            // 로그 추가 또는 예외 처리
            System.out.println("토큰이 존재하지 않습니다: " + refreshToken);
        }
    }
}
