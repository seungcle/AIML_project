package com.group.receiptapp.dto;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupResponse {

    private Long id;
    private String name;
    private List<MemberResponse> members;

    public GroupResponse(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.members = group.getMembers().stream()
                .filter(Member::isActive)
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    // MemberResponse 내부 클래스 또는 별도 DTO 클래스
    @Getter
    @Setter
    public static class MemberResponse {
        private Long id;
        private String name;
        private String email;

        public MemberResponse(Member member) {
            this.id = member.getId();
            this.name = member.getName();
            this.email = member.getEmail();
        }
    }
}