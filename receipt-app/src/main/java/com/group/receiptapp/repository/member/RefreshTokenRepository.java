package com.group.receiptapp.repository.member;

import com.group.receiptapp.domain.token.RefreshToken; // RefreshToken 도메인 클래스
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token); // 리프레시 토큰으로 찾기
    void deleteByToken(String token); // 리프레시 토큰으로 삭제
}