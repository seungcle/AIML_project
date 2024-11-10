package com.group.receiptapp.dto.join;

import com.group.receiptapp.domain.join.JoinRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long groupId;
    private String groupName;
    private String status;

    // JoinRequest 엔티티를 받아 DTO로 변환하는 생성자
    public JoinRequestResponse(Long id, Long memberId, String memberName,  String memberEmail, Long groupId, String groupName, String status) {
        this.id = id;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.groupId = groupId;
        this.groupName = groupName;
        this.status = status;
    }

    // 엔티티에서 DTO로 변환하는 정적 메서드
    public static JoinRequestResponse fromEntity(JoinRequest joinRequest) {
        return new JoinRequestResponse(
                joinRequest.getId(),
                joinRequest.getMember().getId(),
                joinRequest.getMember().getName(),
                joinRequest.getMember().getEmail(),
                joinRequest.getGroup().getId(),
                joinRequest.getGroup().getName(),
                joinRequest.getStatus().name() // ENUM 값인 Status를 String으로 변환
        );
    }
}