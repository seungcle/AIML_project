package com.group.receiptapp.dto.group;
import com.group.receiptapp.domain.group.Group;
import lombok.Getter;

@Getter
public class AdminGroupResponse {
    private Long groupId;
    private String name;
    private Boolean preventDuplicateReceipt;

    public AdminGroupResponse(Group group) {
        this.groupId = group.getId();
        this.name = group.getName();
        this.preventDuplicateReceipt = group.isPreventDuplicateReceipt();
    }
}
