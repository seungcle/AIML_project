// MemberResponse.java
package com.group.receiptapp.dto;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;

public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private boolean isAdmin;
    private boolean isActive;
    private GroupResponse group;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.isAdmin = member.isAdmin();
        this.isActive = member.isActive();
        this.group = new GroupResponse(member.getGroup());
    }

    // Getters and setters if needed
}
