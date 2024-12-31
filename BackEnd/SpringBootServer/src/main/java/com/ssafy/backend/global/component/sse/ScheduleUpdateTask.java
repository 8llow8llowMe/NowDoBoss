package com.ssafy.backend.global.component.sse;

import com.ssafy.backend.global.component.sse.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleUpdateTask {

    private final SseEmitterService sseEmitterService;

    @Scheduled(fixedDelay = 5 * 60 * 1000) // 5분 간격
    public void broadcastPeriodicUpdates() {
        log.info("===================SSE 통신 스케줄러 실행===================");
        if (sseEmitterService.hasActiveEmitter()) {
            sseEmitterService.broadcastUpdates();
        }
    }
}
