package com.group.receiptapp.controller.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import com.group.receiptapp.service.login.LoginService;
import com.group.receiptapp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/logout")
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final LoginService loginService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtUtil.resolveToken(authorizationHeader);

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "유효하지 않은 요청입니다."));
        }

        try {
            String email = jwtUtil.extractUsername(accessToken);
            Long memberId = loginService.getMemberIdByEmail(email);
            notificationService.unsubscribe(memberId);

            return ResponseEntity.ok(Map.of("message", "로그아웃 성공 및 SSE 연결 해제"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "토큰이 존재하지 않습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "로그아웃 처리 중 오류 발생"));
        }
    }

}