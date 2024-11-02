// MemberResponse.java
package com.group.receiptapp.dto;

import com.group.receiptapp.domain.member.Member;

public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private boolean isAdmin;
    private boolean isActive;
    private GroupResponse group;
    private String message; // 오류 메시지를 위한 필드

    // Member 객체로 초기화하는 생성자
    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.isAdmin = member.isAdmin();
        this.isActive = member.isActive();
        // group이 null이 아닌 경우에만 GroupResponse로 변환
        this.group = member.getGroup() != null ? new GroupResponse(member.getGroup()) : null;
    }

    // 오류 메시지를 위한 생성자
    public MemberResponse(String message) {
        this.message = message;
    }

    // Getters and setters for all fields, including message
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isActive() {
        return isActive;
    }

    public GroupResponse getGroup() {
        return group;
    }

    public String getMessage() {
        return message;
    }
}
