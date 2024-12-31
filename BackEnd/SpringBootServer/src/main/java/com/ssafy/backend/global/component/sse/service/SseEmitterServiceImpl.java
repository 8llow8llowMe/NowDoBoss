package com.ssafy.backend.global.component.sse.service;

import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.component.kafka.dto.response.RankingResponse;
import com.ssafy.backend.global.component.kafka.service.KafkaStreamService;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterServiceImpl implements SseEmitterService {

    private final KafkaStreamService kafkaStreamService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter createEmitter() {
        String id = Long.toString(System.currentTimeMillis());
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분
        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));

        sendInitialData(emitter);

        return emitter;
    }

    @Override
    public void broadcastUpdates() {
        RankingResponse rankings = kafkaStreamService.getRankings();
        Message<RankingResponse> message = Message.success(rankings);
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .data(message, MediaType.APPLICATION_JSON)
                    .id(Long.toString(System.currentTimeMillis()))
                    .name("ranking-update")
                    .reconnectTime(10000));
            } catch (IOException e) {
                log.warn("Emitter {} disconnected. Removing emitter.", id);
                emitter.completeWithError(e); // 클라이언트의 연결 문제로 발생한 경우
                emitters.remove(id); // 연결 해제된 Emitter 제거
            }
        });
    }

    @Override
    public boolean hasActiveEmitter() {
        return !emitters.isEmpty();
    }

    private void sendInitialData(SseEmitter emitter) {
        try {
            RankingResponse rankings = kafkaStreamService.getRankings();
            Message<RankingResponse> message = Message.success(rankings);
            emitter.send(message, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            log.error("Error sending initial data", e);
        }
    }


}
