package com.group.receiptapp.web.login;

import com.group.receiptapp.security.JwtUtil; // 필요한 JWT 유틸 클래스
import com.group.receiptapp.service.login.LoginService; // 필요한 서비스 클래스
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    private final LoginService loginService; // Refresh Token 관리 서비스

    public LogoutController(LoginService loginService) {
        this.loginService = loginService; // Refresh Token 관리 서비스 주입
    }

    @PostMapping("/api")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token"); // JSON에서 refresh token 가져오기

        // Refresh Token을 무효화하는 로직
        loginService.invalidateRefreshToken(refreshToken); // 서비스에서 토큰 무효화 처리

        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

}