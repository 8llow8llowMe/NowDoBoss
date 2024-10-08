package com.ssafy.backend.global.component.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.component.jwt.JwtTokenProvider;
import com.ssafy.backend.global.component.jwt.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * JWT 인증을 위한 커스텀 필터입니다.
 * HTTP 요청의 Authorization 헤더에서 JWT 액세스 토큰을 추출하고 검증하여,
 * 유효한 경우 Spring Security의 SecurityContext에 인증 정보를 설정합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenSecurityFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 요청에 대해 필터링 로직을 수행합니다.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException      입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 요청에서 JWT 토큰을 추출합니다.
        String accessToken = getJwtFrom(request);

        // 추출된 토큰이 실제로 텍스트를 포함하고 있는지 확인합니다.
        if (StringUtils.hasText(accessToken)) {
            try {
                // 토큰에서 멤버 정보를 파싱합니다. 유효하지 않은 토큰인 경우 예외를 발생시킵니다.
                MemberLoginActive member = jwtTokenProvider.parseAccessToken(accessToken);

                // 성공적으로 토큰이 파싱되면 로그를 통해 인증된 회원의 ID와 해당 요청의 시도를 기록합니다.
                log.info("회원 ID : {}  - 요청 시도", member.id());
                SecurityContextHolder.getContext().setAuthentication(createAuthenticationToken(member));
            } catch (JwtTokenException e) {
                // JWT 토큰 파싱 중 예외가 발생하면, 보안 컨텍스트를 클리어하고 에러를 응답합니다.
                SecurityContextHolder.clearContext();
                sendError(response, e);
                // 이 후의 필터 체인 처리를 중단하고 메서드를 종료합니다.
                return;
            }
        }

        // 에러 없이 토큰 검증이 완료되면, 다음 필터로 요청과 응답을 전달합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 문자열. 추출할 수 없는 경우 null 반환.
     */
    private String getJwtFrom(HttpServletRequest request) {
        // HTTP 요청 헤더에서 'Authorization' 값을 가져옵니다.
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 요청 URI와 함께 추출된 액세스 토큰 값을 로깅합니다.
        log.info("요청 : {} / 액세스 토큰 값 : {}", request.getRequestURI(), bearerToken);

        // 헤더에 있는 토큰이 'Bearer '로 시작하는 경우, 해당 접두어를 제거하고 실제 토큰만 반환합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        // 'Bearer ' 접두어가 없거나 토큰 자체가 없는 경우 null을 반환합니다.
        return null;
    }

    /**
     * 주어진 사용자 정보를 기반으로 JwtTokenAuthentication을 생성합니다.
     *
     * @param member 사용자 정보
     * @return 생성된 JwtAuthenticationToken 객체
     */
    private JwtTokenAuthentication createAuthenticationToken(MemberLoginActive member) {
        // JwtTokenAuthentication 객체를 생성하고, 사용자의 권한을 설정합니다.
        // 이 권한 정보는 Spring Security의 인증 과정에서 사용됩니다.
        return new JwtTokenAuthentication(member, "",
                List.of(new SimpleGrantedAuthority(member.role().name())));
    }

    /**
     * JWT 예외 발생 시 클라이언트에게 오류 응답을 보냅니다.
     *
     * @param response HTTP 응답 객체
     * @param e        발생한 JwtException
     * @throws IOException 입출력 예외 발생 시
     */
    private void sendError(HttpServletResponse response, JwtTokenException e) throws IOException {
        // 응답의 상태 코드를 예외에서 제공하는 상태 코드로 설정합니다.
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        // 응답의 Content-Type을 'application/json'으로 설정합니다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 응답 본문에 오류 메시지를 JSON 형식으로 작성하고 클라이언트에게 전송합니다.
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Message.fail(e.getErrorCode().name(), e.getMessage())));
        writer.flush();
    }

    /**
     * 특정 경로에 대해 JWT 필터를 적용하지 않도록 설정합니다.
     * 이 메서드를 오버라이드하여 특정 조건에 해당하는 요청을 필터링하지 않도록 할 수 있습니다.
     *
     * @param request 현재 처리 중인 HTTP 요청 객체
     * @return {@code true}이면 필터가 적용되지 않으며, {@code false}이면 필터가 적용됩니다.
     *         여기서는 "/actuator/prometheus" 경로에 대해 필터를 건너뛰도록 설정했습니다.
     * @throws ServletException 서블릿 예외 발생 시
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        // 특정 경로에 대해 필터를 건너뜁니다.
        // 프로메테우스가 메트릭을 가져오는 API 호출 경로는 필터링 되지 않으며, 해당 경로는 doFilterInternal() 로직을 타지 않습니다.
        String requestURI = request.getRequestURI();
        return "/actuator/prometheus".equals(requestURI);
    }
}
