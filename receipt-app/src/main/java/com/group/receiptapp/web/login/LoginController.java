package com.group.receiptapp.web.login;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.service.login.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult result) {
        log.debug("Login attempt with email: {}", form.getEmail()); // 이메일 값이 제대로 전달되는지 확인

        if (result.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getEmail(), form.getPassword());
        log.info("Login attempt for email: {}", form.getEmail());
        log.info("login? {}", loginMember);
        if (loginMember == null) {
            // 로그인 실패 시 처리
            log.warn("Login failed for email: {}", form.getEmail());
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        log.info("Login successful for user: {}", loginMember.getEmail());

        return "redirect:/home";  // 로그인 성공 후 홈으로 이동
    }
}