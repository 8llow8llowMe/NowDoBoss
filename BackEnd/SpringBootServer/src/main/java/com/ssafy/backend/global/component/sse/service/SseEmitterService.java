package com.ssafy.backend.global.component.sse.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterService {

    SseEmitter createEmitter();

    void broadcastUpdates();

    boolean hasActiveEmitter();
}
