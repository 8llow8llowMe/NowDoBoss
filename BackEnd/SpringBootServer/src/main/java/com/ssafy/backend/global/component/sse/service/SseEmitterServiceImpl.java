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

/**
 * SseEmitterServiceImpl 클래스
 *
 * <p>서버-클라이언트 간 실시간 데이터 전송을 관리하는 SSE(Service-Sent Events) 서비스 구현 클래스입니다.
 *
 * <p>Kafka에서 데이터를 가져와 SSE를 통해 구독 중인 클라이언트들에게 전송합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterServiceImpl implements SseEmitterService {

    private final KafkaStreamService kafkaStreamService; // Kafka 데이터를 가져오기 위한 서비스
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>(); // SSE 연결을 관리하는 맵

    /**
     * 새로운 SseEmitter를 생성하고 초기 데이터를 전송합니다.
     *
     * <p>30분 동안 연결 유지 가능하며, 연결이 종료되거나 에러 발생 시 관리 목록에서 제거됩니다.
     *
     * @return 생성된 SseEmitter 객체
     */
    @Override
    public SseEmitter createEmitter() {
        String id = Long.toString(System.currentTimeMillis());
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 동안 연결 유지
        emitters.put(id, emitter);

        // 연결 종료, 타임아웃 또는 에러 발생 시 emitter 제거
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));

        sendInitialData(emitter); // 초기 데이터 전송
        return emitter;
    }

    /**
     * 구독 중인 모든 클라이언트에게 실시간 데이터를 전송합니다.
     *
     * <p>Kafka에서 랭킹 데이터를 가져와 SSE 이벤트로 전송합니다.
     *
     * <p>전송 중 오류가 발생한 클라이언트는 목록에서 제거됩니다.
     */
    @Override
    public void broadcastUpdates() {
        RankingResponse rankings = kafkaStreamService.getRankings(); // 랭킹 데이터 가져오기
        Message<RankingResponse> message = Message.success(rankings);

        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .data(message, MediaType.APPLICATION_JSON) // 데이터 전송
                    .id(Long.toString(System.currentTimeMillis())) // 이벤트 ID
                    .name("ranking-update") // 이벤트 이름
                    .reconnectTime(10000)); // 재연결 간격
            } catch (IOException e) {
                log.warn("Emitter {} disconnected. Removing emitter.", id);
                emitter.completeWithError(e); // 연결 문제 처리
                emitters.remove(id); // 연결 해제된 Emitter 제거
            }
        });
    }

    /**
     * 현재 활성화된 SseEmitter가 존재하는지 확인합니다.
     *
     * @return 활성화된 Emitter가 있으면 true, 없으면 false
     */
    @Override
    public boolean hasActiveEmitter() {
        return !emitters.isEmpty();
    }

    /**
     * 초기 데이터를 전송합니다.
     *
     * <p>SseEmitter 생성 후, 클라이언트에게 첫 데이터를 전송하여 초기 상태를 전달합니다.
     *
     * @param emitter 데이터를 받을 SseEmitter 객체
     */
    private void sendInitialData(SseEmitter emitter) {
        try {
            RankingResponse rankings = kafkaStreamService.getRankings(); // 초기 랭킹 데이터
            Message<RankingResponse> message = Message.success(rankings);
            emitter.send(message, MediaType.APPLICATION_JSON); // 데이터 전송
        } catch (IOException e) {
            log.error("Error sending initial data", e); // 전송 실패 시 로그 출력
        }
    }
}
