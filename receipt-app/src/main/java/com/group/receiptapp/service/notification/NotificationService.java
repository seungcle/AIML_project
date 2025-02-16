package com.group.receiptapp.service.notification;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.notification.Notification;
import com.group.receiptapp.dto.notification.NotificationResponse;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.notification.NotificationRepository;
import com.group.receiptapp.repository.sse.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;
    private final MemberRepository memberRepository;

    // 로그인- SSE 구독 시작
    public SseEmitter subscribe(Long memberId) {
        unsubscribe(memberId);

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 연결 유지
        sseEmitterRepository.addEmitter(memberId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.removeEmitter(memberId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.removeEmitter(memberId, emitter));

        try {
            // 연결 직후 초기 메시지 전송 (Optional)
            emitter.send(SseEmitter.event().name("CONNECTED").data("Subscribed to notifications"));
        } catch (IOException e) {
            log.error("Error during SSE subscription for member {}: {}", memberId, e.getMessage());
        }

        log.info("SSE 구독 시작: memberId={}", memberId);
        return emitter;
    }

    // 로그아웃- SSE 해제
    public void unsubscribe(Long memberId) {
        List<SseEmitter> emitters = sseEmitterRepository.getEmitters(memberId);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("DISCONNECTED").data("Logged out"));
                emitter.complete();
            } catch (IOException e) {
                log.error("SSE 해제 실패: {}", e.getMessage());
            }
        }
        sseEmitterRepository.removeAllEmitters(memberId);
        log.info("SSE 구독 종료: memberId={}", memberId);
    }

    public List<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByMemberIdAndIsReadFalse(memberId);
    }

    @Transactional
    public NotificationResponse markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        log.debug("Before setRead: {}", notification.isRead());
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        log.debug("After setRead: {}", notification.isRead());
        return NotificationResponse.fromEntity(notification);
    }

    @Transactional
    public List<Map<String, Object>> createGroupNotification(Long groupId, Long senderId, String message) {
        log.info("그룹 알림 생성 시작 - 그룹ID: {}, 발신자ID: {}", groupId, senderId);

        // 같은 그룹에 속한 모든 멤버 조회
        List<Member> groupMembers = memberRepository.findByGroupId(groupId);
        log.info("그룹 멤버 목록: {}", groupMembers.stream().map(Member::getId).toList());

        List<Map<String, Object>> notificationResults = new ArrayList<>();

        for (Member member : groupMembers) {
            // 본인(senderId)에게는 알림을 보내지 않음
            if (member.getId().equals(senderId)) {
                log.info("본인 제외 - memberId: {}", member.getId());
                continue;
            }

            // 알림 생성 및 저장
            Notification notification = new Notification();
            notification.setGroupId(groupId);
            notification.setMemberId(member.getId());  // 대상 멤버 ID
            notification.setMessage(message);
            notificationRepository.save(notification);
            log.info("알림 저장 완료 - 대상 memberId: {}, message: {}", member.getId(), message);

            boolean sent = false;
            // SSE로 알림 전송 (해당 멤버가 구독 중이라면)
            List<SseEmitter> emitters = sseEmitterRepository.getEmitters(member.getId());
            log.info("대상 멤버 SSE 구독 수: {}", emitters.size());

            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("NEW_NOTIFICATION")
                            .data(NotificationResponse.fromEntity(notification)));
                    sent = true;
                    log.info("SSE 알림 전송 성공 - memberId: {}", member.getId());
                } catch (IOException e) {
                    log.error("SSE 전송 실패: {}", e.getMessage());
                    sseEmitterRepository.removeEmitter(member.getId(), emitter);
                }
            }


            // 전송 결과 저장
            Map<String, Object> result = Map.of(
                    "memberId", member.getId(),
                    "message", message,
                    "sent", sent
            );
            notificationResults.add(result);
        }
        return notificationResults;
    }
}
