package com.group.receiptapp.dto.notification;

import com.group.receiptapp.domain.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long groupId;
    private Long memberId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getGroupId(),
                notification.getMemberId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
