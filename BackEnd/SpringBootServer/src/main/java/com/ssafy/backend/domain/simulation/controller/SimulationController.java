package com.ssafy.backend.domain.simulation.controller;

import com.ssafy.backend.domain.simulation.dto.request.CreateSimulationRequest;
import com.ssafy.backend.domain.simulation.dto.request.SearchFranchiseeRequest;
import com.ssafy.backend.domain.simulation.dto.request.SimulationRequest;
import com.ssafy.backend.domain.simulation.dto.response.SearchFranchiseeResponse;
import com.ssafy.backend.domain.simulation.dto.response.SimulationDocumentResponse;
import com.ssafy.backend.domain.simulation.dto.response.SimulationResponse;
import com.ssafy.backend.domain.simulation.dto.response.StoreResponse;
import com.ssafy.backend.domain.simulation.service.SimulationService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "창업 시뮬레이션", description = "창업 시뮬레이션 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    @Operation(
        summary = "프랜차이즈 검색",
        description = "프랜차이즈 검색 기능입니다."
    )
    @GetMapping("/franchisee")
    public ResponseEntity<Response<List<SearchFranchiseeResponse>>> searchFranchisee(
        @Validated SearchFranchiseeRequest request) {
        return ResponseEntity.ok()
            .body(Response.success(simulationService.searchFranchisee(request)));
    }

    @Operation(
        summary = "업종별 가게 크기 조회",
        description = "업종별 가게 크기를 조회하는 기능입니다."
    )
    @GetMapping("/store")
    public ResponseEntity<Response<StoreResponse>> selectStoreSize(String serviceCode) {
        return ResponseEntity.ok()
            .body(Response.success(simulationService.selectStoreSize(serviceCode)));
    }

    @Operation(
        summary = "창업 시뮬레이션 결과 조회",
        description = "창업 시뮬레이션 결과를 조회하는 기능입니다."
    )
    @PostMapping
    public ResponseEntity<Response<SimulationResponse>> simulation(
        @RequestBody SimulationRequest request) {
        return ResponseEntity.ok().body(Response.success(simulationService.simulate(request)));
    }

    @Operation(
        summary = "창업 시뮬레이션 결과 저장",
        description = "창업 시뮬레이션 결과를 저장하는 기능입니다."
    )
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> createSimulation(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestBody CreateSimulationRequest request) {

        simulationService.createSimulation(loginActive.id(), request);
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "내 창업 시뮬레이션 결과 목록 조회",
        description = "내 창업 시뮬레이션 결과 목록을 조회하는 기능입니다."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<PageResponse<SimulationDocumentResponse>>> selectSimulation(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        PageResponse<SimulationDocumentResponse> response = simulationService.selectSimulation(
            loginActive.id(), page, size);
        return ResponseEntity.ok().body(Response.success(response));
    }
}
