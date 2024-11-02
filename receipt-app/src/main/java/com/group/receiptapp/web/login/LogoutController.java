package com.group.receiptapp.web.login;

import com.group.receiptapp.service.login.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    private final LoginService loginService; // Refresh Token 관리 서비스

    public LogoutController(LoginService loginService) {
        this.loginService = loginService; // Refresh Token 관리 서비스 주입
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "유효하지 않은 요청입니다."));
        }

        try {
            loginService.invalidateRefreshToken(refreshToken);
            return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "토큰이 존재하지 않습니다."));
        }
    }


}