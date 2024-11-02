package com.group.receiptapp.dto;

import com.group.receiptapp.domain.group.Group;

public class GroupResponse {
    private Long id;
    private String name;

    public GroupResponse(Group group) {
        this.id = group.getId();
        this.name = group.getName();
    }

    // Getter 메서드 추가
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
