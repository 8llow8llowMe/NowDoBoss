package com.ssafy.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${server.fast-api-url}")
    private String fastApiBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(fastApiBaseUrl) // yml 파일에서 주입된 URL 사용
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
