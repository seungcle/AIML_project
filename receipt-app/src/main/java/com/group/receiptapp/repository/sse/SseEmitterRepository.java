package com.group.receiptapp.repository.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterRepository {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEmitter(Long memberId, SseEmitter emitter) {
        emitters.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> getEmitters(Long memberId) {
        return emitters.getOrDefault(memberId, List.of()); // 값이 없으면 빈 리스트 반환
    }

    public void removeEmitter(Long memberId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(memberId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            emitter.complete();
            if (userEmitters.isEmpty()) {
                emitters.remove(memberId);
            }
        }
    }

    public void removeAllEmitters(Long memberId) {
        List<SseEmitter> userEmitters = emitters.remove(memberId);
        if (userEmitters != null) {
            userEmitters.forEach(SseEmitter::complete);
        }
    }
}
