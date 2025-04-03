package com.ssafy.backend.domain.support.controller;

import com.ssafy.backend.domain.support.dto.response.StartupSupportListResponse;
import com.ssafy.backend.domain.support.service.StartupSupportService;
import com.ssafy.backend.global.annotation.PublicEndpoint;
import com.ssafy.backend.global.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "창업지원", description = "창업지원 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/startup-support")
public class StartupSupportController {

    private final StartupSupportService startupSupportService;

    @Operation(
        summary = "창업지원 목록 조회",
        description = "창업지원 목록을 조회하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping
    public ResponseEntity<Response<List<StartupSupportListResponse>>> selectSupport(Long lastId) {
        return ResponseEntity.ok()
            .body(Response.success(startupSupportService.selectSupport(lastId)));
    }
}
