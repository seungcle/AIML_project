package com.group.receiptapp.controller.notification;

import com.group.receiptapp.domain.notification.Notification;
import com.group.receiptapp.dto.notification.NotificationResponse;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.LoginService;
import com.group.receiptapp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    /* 나중에 지워야 함*/
    // 알림 테스트 생성
    @PostMapping("/test")
    public ResponseEntity<Void> createTestGroupNotification(
            @RequestParam Long groupId,
            @RequestParam Long memberId,
            @RequestParam String message) {
        notificationService.createGroupNotification(groupId, memberId, message);
        return ResponseEntity.ok().build();
    }

    // SSE 구독
    @GetMapping("/subscribe/{memberId}")
    public ResponseEntity<SseEmitter> subscribe(
            @PathVariable Long memberId,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = jwtUtil.resolveToken(authorizationHeader);
            if (token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(null);
            }

            String email = jwtUtil.extractUsername(token);
            Long tokenMemberId = loginService.getMemberIdByEmail(email);

            if (!tokenMemberId.equals(memberId)) {
                return ResponseEntity.status(403).body(null);
            }

            return ResponseEntity.ok(notificationService.subscribe(memberId));
        } catch (Exception e) {
            log.error("SSE 구독 오류: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    // 안 읽은 알림 목록 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long memberId,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = jwtUtil.resolveToken(authorizationHeader);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }

            String email = jwtUtil.extractUsername(token);
            Long tokenMemberId = loginService.getMemberIdByEmail(email);

            if (!tokenMemberId.equals(memberId)) {
                return ResponseEntity.status(403).build();
            }

            List<Notification> notifications = notificationService.getUnreadNotifications(memberId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("알림 조회 오류: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 알림 읽음 처리하기
    @PostMapping("/read/{id}")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        NotificationResponse response = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(response);
    }

}
