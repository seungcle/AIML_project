package com.group.receiptapp.config;


import com.group.receiptapp.service.login.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (필요 시 활성화)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/members/add", "/css/**", "/js/**").permitAll() // 인증 없이 접근 가능한 경로
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증을 요구
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email") // 이메일 필드를 username으로 사용
                        .passwordParameter("password") // 기본값이 password이므로 생략 가능
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService) // 사용자 정의 UserDetailsService 설정
                .logout(LogoutConfigurer::permitAll); // 로그아웃 설정

        return http.build(); // SecurityFilterChain 객체 반환
    }


}