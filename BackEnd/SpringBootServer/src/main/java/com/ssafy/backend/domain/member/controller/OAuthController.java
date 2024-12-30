package com.ssafy.backend.domain.member.controller;

import com.ssafy.backend.domain.member.dto.MemberLoginResponse;
import com.ssafy.backend.domain.member.service.OAuthService;
import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.component.oauth.vendor.enums.OAuthDomain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@Tag(name = "소셜(회원)", description = "소셜(회원) 관련 API 입니다.")
public class OAuthController {

    private final OAuthService oAuthService;

    @Operation(
        summary = "소셜 도메인을 통한 인증코드 가져오기",
        description = "소셜 로그인 하기 전 인증코드를 가져와 소셜에 가입된 회원정보를 가져오는 기능입니다."
    )
    @GetMapping("/{oAuthDomain}")
    public ResponseEntity<Message<String>> provideAuthCodeRequestUrlOAuth(
        @PathVariable OAuthDomain oAuthDomain) {
        String redirectUrl = oAuthService.provideAuthCodeRequestUrlOAuth(oAuthDomain);
        return ResponseEntity.ok().body(Message.success(redirectUrl));
    }

    @Operation(
        summary = "소셜 로그인(회원가입)",
        description = "인증코드를 통해 소셜 로그인(회원가입)을 하는 기능입니다."
    )
    @GetMapping("/{oAuthDomain}/login")
    public ResponseEntity<Message<MemberLoginResponse>> loginOAuth(
        @PathVariable("oAuthDomain") OAuthDomain oAuthDomain,
        @RequestParam("code") String authCode,
        HttpServletResponse response) {
        MemberLoginResponse loginResponse = oAuthService.loginOAuth(oAuthDomain, authCode);
        // JWT 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken",
            loginResponse.tokenInfo().accessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(25200); // 4200분(25200초)으로 설정 (25200)
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().body(Message.success(loginResponse));
    }

}
