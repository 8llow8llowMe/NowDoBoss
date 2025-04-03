package com.ssafy.backend.domain.share.controller;

import com.ssafy.backend.domain.share.dto.request.CreateShareRequest;
import com.ssafy.backend.domain.share.dto.response.LinkTokenResponse;
import com.ssafy.backend.domain.share.dto.response.ShareResponse;
import com.ssafy.backend.domain.share.service.ShareService;
import com.ssafy.backend.global.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공유하기", description = "공유하기 관련 API 입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share")
public class ShareController {

    private final ShareService shareService;

    @Operation(
        summary = "공유 데이터 저장",
        description = "공유 데이터를 저장하는 기능입니다."
    )
    @PostMapping
    public ResponseEntity<Response<LinkTokenResponse>> share(
        @Validated @RequestBody CreateShareRequest request) {
        Map<String, Object> inputs = request.getInput();
        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            log.info("key : {}, value : {}", entry.getKey(), entry.getValue());
        }

        LinkTokenResponse response = shareService.createShare(request);
        return ResponseEntity.ok().body(Response.success(response));
    }

    @Operation(
        summary = "공유 데이터 조회",
        description = "공유 데이터를 조회하는 기능입니다."
    )
    @GetMapping("/{token}")
    public ResponseEntity<Response<ShareResponse>> selectShare(@PathVariable String token) {
        return ResponseEntity.ok().body(Response.success(shareService.selectShare(token)));
    }
}
