package com.group.receiptapp.domain.token;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId; // 회원 ID
    private String token; // 리프레시 토큰
    private boolean isActive; // 토큰 활성화 여부

    public RefreshToken() {}

    public RefreshToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
        this.isActive = true; // 기본값으로 활성화 상태를 true로 설정
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
