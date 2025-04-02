package com.ssafy.backend.global.component.jwt;

import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.entity.enums.MemberRole;
import com.ssafy.backend.global.component.jwt.exception.JwtTokenErrorCode;
import com.ssafy.backend.global.component.jwt.exception.JwtTokenException;
import com.ssafy.backend.global.component.jwt.security.MemberLoginActive;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JwtTokenProvider는 JWT 토큰의 생성과 검증을 담당하는 컴포넌트입니다.
 * 액세스 토큰과 리프레시 토큰을 발급하고, 액세스 토큰으로부터 사용자 정보를 파싱합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_NICKNAME = "nickname";
    private static final String CLAIM_PROFILE_IMAGE = "profileImage";
    private static final String CLAIM_ROLE = "role";
    private final JwtTokenPropsInfo tokenPropsInfo;

    /**
     * 사용자 정보를 기반으로 액세스 토큰을 발급합니다.
     *
     * @param member 사용자 정보가 담긴 Member 객체
     * @return 발급된 액세스 토큰 문자열
     */
    public String issueAccessToken(Member member) {
        // 사용자 정보를 기반으로 Claims 생성
        Claims claims = Jwts.claims()
            .id(UUID.randomUUID().toString())
            .subject(String.valueOf(member.getId()))
            .add(CLAIM_EMAIL, member.getEmail())
            .add(CLAIM_NAME, member.getName())
            .add(CLAIM_NICKNAME, member.getNickname())
            .add(CLAIM_PROFILE_IMAGE, member.getProfileImage())
            .add(CLAIM_ROLE, member.getRole())
            .build();

        // 생성된 Claims와 함께 액세스 토큰 발급
        return issueToken(claims, tokenPropsInfo.accessExpiration(), tokenPropsInfo.accessKey());
    }

    /**
     * 리프레시 토큰을 발급합니다.
     *
     * @return 발급된 리프레시 토큰 문자열
     */
    public String issueRefreshToken() {
        // Claims 없이 리프레시 토큰만 발급
        return issueToken(null, tokenPropsInfo.refreshExpiration(), tokenPropsInfo.refreshKey());
    }

    /**
     * 액세스 토큰을 파싱하여 회원 정보를 포함한 MemberLoginActive 객체를 반환합니다.
     *
     * @param accessToken 파싱할 액세스 토큰 문자열
     * @return 파싱된 사용자 정보가 담긴 MemberLoginActiveRecord 객체
     */
    public MemberLoginActive parseAccessToken(String accessToken) {
        // 액세스 토큰 발급
        Claims payload = parseToken(accessToken, tokenPropsInfo.accessKey());

        // 파싱된 데이터를 기반으로 MemberLoginActive 객체 생성 및 반환
        return new MemberLoginActive(
            Long.valueOf(payload.getSubject()),
            payload.get(CLAIM_EMAIL, String.class),
            payload.get(CLAIM_NAME, String.class),
            payload.get(CLAIM_NICKNAME, String.class),
            MemberRole.fromName(payload.get(CLAIM_ROLE, String.class))
        );
    }

    /**
     * 토큰을 발급하는 내부 메서드입니다.
     * 주어진 클레임(claims), 만료 기간(expiration), 비밀 키(secretKey)를 사용하여 JWT 토큰을 생성합니다.
     *
     * @param claims     토큰에 포함될 정보를 담고 있는 Claims 객체. 액세스 토큰 발급 시 사용자 정보가 포함되며, 리프레시 토큰 발급 시에는 null일
     *                   수 있습니다.
     * @param expiration 토큰의 만료 시간을 정의하는 Duration 객체입니다. 이 값을 기반으로 토큰의 'exp' (만료 시간) 클레임이 설정됩니다.
     * @param secretKey  토큰을 서명하는 데 사용되는 비밀 키입니다. 이 키는 토큰의 무결성을 보장하는 데 중요한 역할을 합니다.
     * @return 생성된 JWT 토큰 문자열을 반환합니다. 이 토큰은 이후 사용자 인증에 사용될 수 있습니다.
     */
    private String issueToken(Claims claims, Duration expiration, String secretKey) {
        Date now = new Date();
        // 토큰 발급 시 현재 시간과 만료 시간을 설정하여 JWT를 생성합니다.
        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expiration.toMillis()))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
            .compact();
    }

    /**
     * 주어진 JWT 토큰을 파싱하여 클레임(Claims)을 추출하는 내부 메서드입니다.
     * 이 메서드는 액세스 토큰의 유효성 검사와 사용자 정보의 추출 과정에서 사용됩니다.
     *
     * @param token     파싱할 JWT 토큰 문자열입니다.
     * @param secretKey 토큰 파싱 시 사용되는 비밀 키입니다. 이 키는 토큰이 서명된 것과 동일한 키여야 하며, 토큰의 무결성 검증에 사용됩니다.
     * @return 파싱된 토큰으로부터 추출된 Claims 객체를 반환합니다. 이 객체는 토큰에 포함된 정보(예: 사용자 ID, 이메일 등)에 접근하는 데 사용될 수 있습니다.
     * @throws JwtTokenException 토큰이 만료되었거나, 형식이 잘못되었거나, 서명이 유효하지 않는 경우 JwtTokenException을 발생시킵니다. 이
     *                           예외는 토큰의 유효성 문제를 나타냅니다.
     */
    private Claims parseToken(String token, String secretKey) {
        Claims payload;

        try {
            // 토큰을 파싱하여 payload를 반환합니다. 이 과정에서 토큰의 무결성과 유효성이 검증됩니다.
            payload = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            // 토큰 만료 예외 처리
            throw new JwtTokenException(JwtTokenErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
            // 토큰 형식 불일치 예외 처리
            throw new JwtTokenException(JwtTokenErrorCode.INVALID_TOKEN);
        } catch (SignatureException e) {
            // 토큰 서명 검증 실패 예외 처리
            throw new JwtTokenException(JwtTokenErrorCode.SIGNATURE_INVALID);
        }

        return payload;
    }
}
