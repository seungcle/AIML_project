package com.group.receiptapp.security;

import com.group.receiptapp.service.login.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${spring.jwt.secret-key}") // application.yml에서 비밀 키를 가져옴
    private String secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = secretKey.trim(); // 필요 시 공백 제거
    }

    // Access Token 생성 (유효기간: 10시간)
    public String generateToken(String username) {
        return createToken(new HashMap<>(), username, 1000 * 60 * 60 * 10);
    }

    // Refresh Token 생성 (유효기간: 30일)
    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, 1000 * 60 * 60 * 24 * 30);
    }

    // JWT 생성
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰 만료 확인
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 토큰 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // WT Claims에서 특정 데이터 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 Claims 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 유효성 검사
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Authorization Header에서 토큰 추출
    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

}
