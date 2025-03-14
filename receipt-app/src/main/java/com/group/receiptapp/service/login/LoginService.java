package com.group.receiptapp.service.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.token.RefreshToken;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.member.RefreshTokenRepository;
import com.group.receiptapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
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

    @Transactional(readOnly = true)
    public Long getMemberIdByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다."));
    }

    // 리프레시 토큰 발급
    public String generateRefreshToken(Member member) {
        // 기존 리프레시 토큰이 존재하면 삭제
        refreshTokenRepository.findByMemberId(member.getId()).ifPresent(existingToken ->
                refreshTokenRepository.delete(existingToken)
        );

        // 새 리프레시 토큰 생성 및 저장
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());
        RefreshToken tokenEntity = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(tokenEntity);
        return refreshToken;
    }

    @Transactional
    public void invalidateRefreshTokenByMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
