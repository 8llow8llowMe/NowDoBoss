package com.ssafy.backend.global.component.shutdown;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GracefulShutdownHandler {

    @PreDestroy
    public void onShutdown() {
        log.info("[SHUTDOWN] 애플리케이션 종료 감지됨. Graceful Shutdown 시작...");
    }

}
