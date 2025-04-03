package com.ssafy.backend.global.config;

import com.ssafy.backend.global.component.oauth.vendor.enums.OAuthDomainConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC에 대한 추가적인 설정을 정의하는 클래스입니다.
 * 여기서는 애플리케이션 내에서 사용되는 컨버터와 포맷터를 등록할 수 있습니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 애플리케이션의 컨버터와 포맷터를 등록합니다.
     * 여기서는 OAuthDomain 열거형을 문자열로부터 변환하는 컨버터를 등록합니다.
     *
     * @param registry 포맷터 등록을 위한 레지스트리
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OAuthDomainConverter()); // OAuthDomain 컨버터 등록
    }
}

