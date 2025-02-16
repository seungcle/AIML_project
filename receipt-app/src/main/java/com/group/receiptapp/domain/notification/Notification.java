package com.group.receiptapp.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId; // 그룹 ID
    private Long memberId; // 알림을 받을 멤버 ID
    private String message; // 알림 내용

    @Column(name = "is_read") // 데이터베이스의 is_read 컬럼과 매핑
    private boolean isRead = false; // 읽음 여부

    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}