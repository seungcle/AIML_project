package com.group.receiptapp.config;


import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import com.group.receiptapp.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil; // JwtUtil 추가

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil; // JwtUtil 주입
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
                        .requestMatchers("/", "/add", "/login", "/members/add", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/login/api").permitAll() // POST /login/api에 대한 접근 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증을 요구
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email") // 이메일 필드를 username으로 사용
                        .passwordParameter("password") // 기본값이 password이므로 생략 가능
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(86400)  // 쿠키 유효시간 1일
                )
                .userDetailsService(customUserDetailsService) // 사용자 정의 UserDetailsService 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login?sessionExpired=true") // 세션이 유효하지 않을 경우 리다이렉트// 세션이 필요한 경우에만 생성
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID", "remember-me") // 쿠키 삭제
                        .permitAll()
                );


        return http.build(); // SecurityFilterChain 객체 반환
    }

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // 세미콜론 허용
        return firewall;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService); // 인자를 전달하여 생성
    }

}

