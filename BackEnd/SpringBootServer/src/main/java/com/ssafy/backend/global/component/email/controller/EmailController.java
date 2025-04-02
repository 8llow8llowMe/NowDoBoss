package com.ssafy.backend.global.component.email.controller;

import com.ssafy.backend.global.common.dto.Response;
import com.ssafy.backend.global.component.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "이메일", description = "이메일 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;

    @Operation(
        summary = "이메일 인증코드 발송",
        description = "회원가입 전 해당 이메일에 중복된 이메일 확인 및 인증코드를 발송하는 기능입니다."
    )
    @PostMapping("/send/{memberEmail}")
    public Mono<ResponseEntity<Response<Void>>> sendEmailCode(@PathVariable String memberEmail) {
        return emailService.sendEmailCode(memberEmail)
            .then(Mono.just(ResponseEntity.ok().body(Response.success())));
    }

    @Operation(
        summary = "이메일 인증코드 검증",
        description = "발송된 이메일 인증코드와 입력한 이메일 인증코드가 일치하는지 검증하는 기능입니다."
    )
    @PostMapping("/verify/{memberEmail}/{emailCode}")
    public ResponseEntity<Response<Void>> verifyEmailCode(@PathVariable String memberEmail,
        @PathVariable String emailCode) {
        emailService.verifyEmailCode(memberEmail, emailCode);
        return ResponseEntity.ok().body(Response.success());
    }
}
