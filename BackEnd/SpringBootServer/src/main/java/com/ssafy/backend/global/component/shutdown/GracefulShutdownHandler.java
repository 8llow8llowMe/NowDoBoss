package com.ssafy.backend.global.component.shutdown;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GracefulShutdownHandler {

    private final HikariDataSource dataSource;

    @PreDestroy
    public void onShutdown() {
        log.info("[SHUTDOWN] 애플리케이션 종료 감지됨. Graceful Shutdown 시작...");

        try {
            if (dataSource != null && !dataSource.isClosed()) {
                log.info("[SHUTDOWN] HikariCP 커넥션 풀을 종료합니다...");
                dataSource.close();  // 여기서 DB 커넥션 정리
                log.info("[SHUTDOWN] 커넥션 풀 종료 완료.");
            }
        } catch (Exception e) {
            log.error("[SHUTDOWN] 커넥션 풀 종료 중 예외 발생", e);
        }

        log.info("[SHUTDOWN] Graceful Shutdown 작업 완료.");
    }

}
