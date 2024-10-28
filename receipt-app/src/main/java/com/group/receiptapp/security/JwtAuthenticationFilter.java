package com.group.receiptapp.security;

import com.group.receiptapp.service.login.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Lombok SLF4J 로거 사용
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j // 로그를 사용할 수 있게 설정
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 JWT 추출
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // JWT가 "Bearer " 접두어로 시작하는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearer " 다음 부분을 추출
            try {
                username = jwtUtil.extractUsername(jwt); // JWT에서 사용자 이름 추출
            } catch (ExpiredJwtException e) {
                // 토큰이 만료되었을 경우 처리
                log.warn("JWT token is expired: {}", e.getMessage()); // logger.warn 변경
            } catch (Exception e) {
                log.error("JWT token is invalid: {}", e.getMessage(), e); // 추가적인 오류 로깅
            }
        }

        // 사용자 이름이 존재하고 SecurityContext에 인증 정보가 없는 경우
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 사용자 정보를 로드하고 인증 설정
            var userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) { // JWT 유효성 검증
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }
}
