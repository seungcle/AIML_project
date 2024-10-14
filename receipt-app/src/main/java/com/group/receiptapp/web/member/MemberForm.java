package com.group.receiptapp.web.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    private String name;
    private String email;
    private String password;
    private Long groupId;
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
