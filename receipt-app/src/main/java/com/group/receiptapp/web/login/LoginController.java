package com.group.receiptapp.web.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller // HTML 렌더링을 위해 Controller 사용
@Slf4j
@RequestMapping("/login") // 경로 지정
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil; // JwtUtil 변수 선언

    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService; // LoginService 주입
        this.jwtUtil = jwtUtil; // JwtUtil 주입
    }

    @GetMapping
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm"; // HTML 폼 렌더링
    }

    @PostMapping("/api") // API 엔드포인트
    public ResponseEntity<Map<String, Object>> loginApi(@Valid @RequestBody LoginForm form, BindingResult result) {
        log.debug("Login attempt with email: {}", form.getEmail()); // 로그인을 시도한 이메일 로그 출력

        // 로그인 서비스 호출하여 사용자 인증
        Member loginMember = loginService.login(form.getEmail(), form.getPassword());
        log.info("Login attempt for email: {}", form.getEmail());

        if (loginMember == null) {
            // 로그인 실패 시
            log.warn("Login failed for email: {}", form.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "아이디 또는 비밀번호가 맞지 않습니다.")); // 오류 응답
        }

        // 로그인 성공 시 토큰 생성
        String refreshToken = loginService.generateRefreshToken(loginMember); // 리프레시 토큰 생성 및 DB 저장
        String accessToken = jwtUtil.generateToken(loginMember.getEmail()); // JWT 생성

        // JSON 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("id", loginMember.getId());
        response.put("email", loginMember.getEmail());
        response.put("refresh_token", refreshToken);
        response.put("access_token", accessToken);

        return ResponseEntity.ok(response); // JSON 응답 반환
    }

    @PostMapping // HTML 폼 제출 처리
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult result) {
        log.debug("Login attempt with email: {}", form.getEmail());

        if (result.hasErrors()) {
            return "login/loginForm"; // 입력 오류가 있으면 로그인 폼으로 돌아감
        }

        Member loginMember = loginService.login(form.getEmail(), form.getPassword());
        log.info("Login attempt for email: {}", form.getEmail());

        if (loginMember == null) {
            // 로그인 실패 시 처리
            log.warn("Login failed for email: {}", form.getEmail()); // 경고 로그
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm"; // 로그인 실패 시 다시 폼으로
        }

        // 로그인 성공 처리
        log.info("Login successful for user: {}", loginMember.getEmail());
        return "redirect:/home";  // 로그인 성공 후 홈으로 이동
    }
}
