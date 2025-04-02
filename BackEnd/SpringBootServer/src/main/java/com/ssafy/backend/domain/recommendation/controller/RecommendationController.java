package com.ssafy.backend.domain.recommendation.controller;

import com.ssafy.backend.domain.recommendation.document.RecommendationDocument;
import com.ssafy.backend.domain.recommendation.dto.response.RecommendationResponse;
import com.ssafy.backend.domain.recommendation.service.RecommendationService;
import com.ssafy.backend.global.common.dto.PageResponse;
import com.ssafy.backend.global.common.dto.Response;
import com.ssafy.backend.global.component.jwt.security.MemberLoginActive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "추천", description = "추천 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(
        summary = "해당 유저에게 맞는 상권 추천",
        description = "해당 유저가 원하는 영역의 상권들을 추천"
    )
    @GetMapping("/{districtCode}/{administrationCode}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Response<List<RecommendationResponse>>>> getCommercialRecommendation(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @PathVariable String districtCode,
        @PathVariable String administrationCode) {

        return recommendationService.getTopThreeRecommendations(districtCode, administrationCode,
                loginActive.id())
            .map(responses -> ResponseEntity.ok().body(Response.success(responses)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "상권 추천 정보 보관함 저장",
        description = "해당 추천 정보를 보관함에 저장"
    )
    @PostMapping("/{districtCode}/{administrationCode}/{commercialCode}/save")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<String>> saveCommercialRecommendation(
        @AuthenticationPrincipal MemberLoginActive loginActive, @PathVariable String districtCode,
        @PathVariable String commercialCode, @PathVariable String administrationCode) {
        recommendationService.saveCommercialRecommendation(commercialCode, loginActive.id());
        return ResponseEntity.ok().body(Response.success("보관함 저장 성공!"));
    }

    @Operation(
        summary = "상권 추천 정보 보관함 저장 취소",
        description = "해당 추천 정보를 보관함에서 삭제"
    )
    @DeleteMapping("/{districtCode}/{administrationCode}/{commercialCode}/cancel")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<String>> deleteCommercialRecommendation(
        @AuthenticationPrincipal MemberLoginActive loginActive, @PathVariable String districtCode,
        @PathVariable String commercialCode, @PathVariable String administrationCode) {
        recommendationService.deleteCommercialRecommendation(commercialCode, loginActive.id());
        return ResponseEntity.ok().body(Response.success("보관함 삭제 성공!"));
    }

    @Operation(
        summary = "보관함에 저장된 상권 추천 리스트 조회",
        description = "해당 유저가 저장한 추천 상권 리스트 목록 조회"
    )
    @GetMapping("/save/list")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<PageResponse<RecommendationDocument>>> getSavedCommercialRecommendationList(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(Response.success(
            recommendationService.getSavedCommercialRecommendationList(loginActive.id(), page,
                size)));
    }

    @Operation(
        summary = "보관함에 저장된 상권 추천 리스트에서 특정 추천 상권 정보 조회",
        description = "해당 유저가 저장한 추천 상권 리스트에서 특정 추천 상권 정보 조히"
    )
    @GetMapping("/save/detail/{commercialCode}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<RecommendationResponse>>> getSavedCommercialRecommendationDetail(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @PathVariable String commercialCode) {
        return ResponseEntity.ok().body(Response.success(
            recommendationService.getSavedCommercialDetail(loginActive.id(), commercialCode)));
    }
}
