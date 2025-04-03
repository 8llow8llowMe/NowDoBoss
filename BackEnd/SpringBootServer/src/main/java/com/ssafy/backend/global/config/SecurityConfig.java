package com.ssafy.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.global.component.jwt.JwtTokenProvider;
import com.ssafy.backend.global.component.jwt.security.JwtTokenSecurityFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security의 구성을 정의하는 설정 클래스입니다.
 * 이 클래스는 JWT 인증을 포함한 Spring Security의 여러 보안 관련 설정을 구성합니다.
 * {@link EnableMethodSecurity} 어노테이션은 메소드 단위의 보안 주석을 활성화하여
 * 세밀한 접근 제어를 가능하게 합니다.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;    // JWT 토큰 생성 및 검증을 담당하는 컴포넌트
    private final ObjectMapper objectMapper;    // JSON 객체 변환을 위한 ObjectMapper

    /**
     * Spring Security의 HTTP 보안 설정을 구성하는 메서드입니다.
     * 이 메서드는 CORS 설정, CSRF 보호 비활성화, HTTP 기본 인증 비활성화,
     * 폼 기반 로그인과 로그아웃 비활성화, JWT 인증 필터 추가 등의 보안 관련 설정을 정의합니다.
     *
     * @param http HttpSecurity 객체를 통해 웹 보안 설정을 구성할 수 있습니다.
     * @return 구성된 SecurityFilterChain 객체를 반환합니다.
     * @throws Exception 보안 설정 중 발생할 수 있는 예외를 처리합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS(Cross-Origin Resource Sharing) 설정을 적용합니다.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // HTTP Basic 인증 방식을 비활성화합니다. (ID/PW 기반 인증 사용하지 않음)
            .httpBasic(AbstractHttpConfigurer::disable)

            // X-Frame-Options 비활성화 (H2 Console 접근 등 필요시 사용)
            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

            // 모든 HTTP 요청에 대해 접근을 허용합니다.
            // 인증이 필요한 요청은 JwtTokenSecurityFilter에서 직접 토큰 검증을 수행하며,
            // @PreAuthorize 등 메서드 수준의 인가 처리는 EnableMethodSecurity에 의해 적용됩니다.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Spring Security 기본 로그인/로그아웃 기능 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)

            // UsernamePasswordAuthenticationFilter 실행 전에 커스텀 JWT 필터를 삽입
            .addFilterBefore(jwtSecurityFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 웹 보안을 커스터마이즈하는 WebSecurityCustomizer 빈을 생성합니다.
     * 이 설정을 통해 특정 요청 경로에 대한 보안 검사를 무시할 수 있습니다.
     *
     * @return WebSecurityCustomizer 객체
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/swagger-ui/**",
            "/v3/api-docs/**"
        );
    }

    /**
     * JWT 인증 필터를 생성하는 메소드입니다.
     * 이 필터는 HTTP 요청의 헤더에서 JWT를 추출하고 검증하는 역할을 합니다.
     *
     * @return JwtTokenSecurityFilter 객체
     */
    @Bean
    public JwtTokenSecurityFilter jwtSecurityFilter() {
        return new JwtTokenSecurityFilter(jwtTokenProvider, objectMapper);
    }

    /**
     * CORS 설정을 위한 CorsConfigurationSource 객체를 생성하는 메소드입니다.
     * 이 설정을 통해 서버는 다른 출처에서 온 요청을 안전하게 처리할 수 있습니다.
     *
     * @return CorsConfigurationSource 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = getCorsConfiguration(3600L);
        // CORS 구성을 URL 패턴에 매핑합니다. 이 예에서는 애플리케이션의 모든 경로("/**")에 대해 CORS 구성을 적용합니다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * CORS 필터를 스프링 부트 애플리케이션의 필터 체인에 등록합니다.
     * 이를 통해 모든 들어오는 요청에 대해 CORS 정책이 적용되도록 합니다.
     *
     * @return FilterRegistrationBean 객체로, 스프링 부트가 관리하는 필터 체인에 CORS 필터를 등록하기 위해 사용됩니다.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsConfiguration config = getCorsConfiguration(6000L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 애플리케이션의 모든 경로("/**")에 대해 CORS 구성을 적용합니다.
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(
            new CorsFilter(source));
        // 필터 체인에서의 실행 순서를 설정합니다. 숫자가 낮을수록 먼저 실행됩니다.
        filterBean.setOrder(0); // 필터 체인에서의 순서 설정
        return filterBean;
    }

    /**
     * CORS 정책 구성을 위한 CorsConfiguration 객체를 생성하고 구성합니다.
     * 이 메서드는 클라이언트 Origin에 따라 접근 허용 여부를 제어하며,
     * 자격 증명(Credentials) 허용 여부, 허용 메서드, 허용 헤더 등을 설정합니다.
     * <p>
     * <p>- 로컬 개발 (Vite 기반: http://localhost:5173)
     * <p>- 운영 환경 (https://www.nowdoboss.com)
     * 두 환경에서 정상 작동하도록 설정되어 있습니다.
     *
     * @param maxAge 프리플라이트 요청의 캐시 지속 시간 (초). OPTIONS 요청 최적화에 사용됩니다.
     * @return 구성된 CorsConfiguration 객체
     */
    private CorsConfiguration getCorsConfiguration(long maxAge) {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "https://www.nowdoboss.com"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(maxAge);
        return config;
    }

    /**
     * 암호화된 비밀번호를 생성하고 검증하는 PasswordEncoder 빈을 생성합니다.
     * 이 빈은 Spring Security에서 제공하는 BCryptPasswordEncoder를 사용합니다.
     *
     * @return PasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용한 패스워드 암호화 객체 생성하여 반환합니다.
    }

}
