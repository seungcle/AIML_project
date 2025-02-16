package com.group.receiptapp.service.controller.notification;


import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.notification.Notification;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.notification.NotificationRepository;
import com.group.receiptapp.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
public class NotificationControllerTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        Member member = new Member();
        member.setId(1L);
        member.setName("Test User");
        member.setEmail("test@example.com");
        member.setPassword("password123");
        memberRepository.save(member);

        notificationRepository.deleteAll();
        Notification notification = new Notification();
        notification.setGroupId(1L);
        notification.setMemberId(1L);
        notification.setMessage("Test Notification");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Test
    void markAsRead_shouldUpdateNotificationStatus() {
        Long notificationId = notificationRepository.findAll().get(0).getId();
        notificationService.markNotificationAsRead(notificationId);
        Notification updatedNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        assertTrue(updatedNotification.isRead(), "Notification should be marked as read");
    }
}