package com.ssafy.backend.domain.member.controller;

import com.ssafy.backend.domain.member.dto.MemberInfo;
import com.ssafy.backend.domain.member.dto.MemberLoginRequest;
import com.ssafy.backend.domain.member.dto.MemberLoginResponse;
import com.ssafy.backend.domain.member.dto.MemberPasswordChangeRequest;
import com.ssafy.backend.domain.member.dto.MemberSignupRequest;
import com.ssafy.backend.domain.member.dto.MemberUpdateRequest;
import com.ssafy.backend.domain.member.service.MemberService;
import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.component.jwt.security.MemberLoginActive;
import com.ssafy.backend.global.component.jwt.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "회원", description = "회원 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Operation(
        summary = "회원가입",
        description = "회원정보에 필요한 정보를 입력하여 회원가입을 하는 기능입니다."
    )
    @PostMapping("/signup")
    public ResponseEntity<Message<Void>> signupMember(
        @Valid @RequestBody MemberSignupRequest signupRequest) {
        memberService.signupMember(signupRequest);
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호를 입력하여 로그인을 하는 기능입니다."
    )
    @PostMapping("/login")
    public ResponseEntity<Message<MemberLoginResponse>> loginMember(
        @RequestBody MemberLoginRequest loginRequest,
        HttpServletResponse response) {

        MemberLoginResponse loginResponse = memberService.loginMember(loginRequest);
        // JWT 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken",
            loginResponse.tokenInfo().accessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(25200); // 4200분(25200초)으로 설정 (25200)
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().body(Message.success(loginResponse));
    }

    @Operation(
        summary = "로그아웃",
        description = "로그인 한 회원을 로그아웃을 하는 기능입니다."
    )
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> logoutMember(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        HttpServletResponse response) {
        memberService.logoutMember(loginActive.email());
        // 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "회원정보 불러오기",
        description = "비밀번호를 제외한 회원가입때 입력한 정보를 불러오는 기능입니다."
    )
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<MemberInfo>> getMember(
        @AuthenticationPrincipal MemberLoginActive loginActive) {
        MemberInfo info = memberService.getMember(loginActive.id());
        return ResponseEntity.ok().body(Message.success(info));
    }

    @Operation(
        summary = "회원 탈퇴하기",
        description = "해당 서비스에 가입한 회원의 회원정보를 삭제하는 기능입니다."
    )
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> deleteMember(
        @AuthenticationPrincipal MemberLoginActive loginActive) {
        memberService.deleteMember(loginActive.id());
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "회원 수정하기",
        description = "회원 정보를 수정(닉네임, 프로필 이미지)하는 기능입니다."
    )
    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> updateImageAndNicknameMember(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestBody MemberUpdateRequest updateRequest) {
        memberService.updateProfileImageAndNickNameMember(loginActive.id(), updateRequest);
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "비밀번호 변경하기",
        description = "회원이 현재 비밀번호, 비밀번호 변경, 비밀번호 변경 확인을 입력하여 비밀번호를 변경하는 기능입니다."
    )
    @PatchMapping("/password/change")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> updatePasswordMember(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @Valid @RequestBody MemberPasswordChangeRequest passwordChangeRequest) {
        memberService.updatePasswordMember(loginActive.id(), passwordChangeRequest);
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "Access 토큰 재발급 받기",
        description = "Access 토큰이 만료된 회원이 레디스에 저장된 Refresh 토큰을 이용하여 Access 토큰을 재발급 받는 기능입니다."
    )
    @PostMapping("/reissue/accessToken/{memberEmail}")
    public ResponseEntity<Message<String>> reissueAccessToken(@PathVariable String memberEmail) {
        String reissueAccessToken = jwtTokenService.reissueAccessToken(memberEmail);
        return ResponseEntity.ok().body(Message.success(reissueAccessToken));
    }
}
