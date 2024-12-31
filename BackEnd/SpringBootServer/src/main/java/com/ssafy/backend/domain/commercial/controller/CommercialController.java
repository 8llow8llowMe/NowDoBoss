package com.ssafy.backend.domain.commercial.controller;

import com.ssafy.backend.domain.commercial.dto.request.CommercialAnalysisSaveRequest;
import com.ssafy.backend.domain.commercial.dto.request.ConversionCodeNameRequest;
import com.ssafy.backend.domain.commercial.dto.response.AllIncomeResponse;
import com.ssafy.backend.domain.commercial.dto.response.AllSalesResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationAreaResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAnalysisResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAreaResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFacilityResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFootTrafficResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialIncomeResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialPopulationResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialServiceResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialStoreResponse;
import com.ssafy.backend.domain.commercial.dto.response.ConversionCodeResponse;
import com.ssafy.backend.domain.commercial.service.CommercialService;
import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.common.dto.PageResponse;
import com.ssafy.backend.global.component.jwt.security.MemberLoginActive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상권", description = "상권 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commercial")
public class CommercialController {

    private final CommercialService commercialService;

    @Operation(
        summary = "자치구, 행정동, 상권 코드명 조회",
        description = "자치구, 행정도, 상권 코드를 통해 코드명을 조회하는 기능입니다."
    )
    @GetMapping("/code-name")
    public ResponseEntity<Message<ConversionCodeResponse>> getCodeByCodeName(
        @Validated ConversionCodeNameRequest request) {

        ConversionCodeResponse response = commercialService.conversionCodeNameToCode(request);

        return ResponseEntity.ok().body(Message.success(response));
    }

    @Operation(
        summary = "해당 자치구에 포함된 행정동 목록 조회",
        description = "해당 자치구에 포함된 행정동 목록을 조회하는 기능입니다."
    )
    @GetMapping("/administration/district/{districtCode}/areas")
    public ResponseEntity<Message<List<CommercialAdministrationResponse>>> getAdministrativeAreasByDistrict(
        @PathVariable String districtCode) {
        List<CommercialAdministrationResponse> administrationAreaResponseList = commercialService.getAdministrativeAreasByDistrict(
            districtCode);
        return ResponseEntity.ok().body(Message.success(administrationAreaResponseList));
    }

    @Operation(
        summary = "해당 행정동에 포함된 상권 목록 조회",
        description = "해당 행정동에 포함된 상권 목록을 조회하는 기능입니다."
    )
    @GetMapping("/administration/{administrationCode}/areas")
    public ResponseEntity<Message<List<CommercialAreaResponse>>> getCommercialAreasByAdministrationCode(
        @PathVariable String administrationCode) {
        List<CommercialAreaResponse> areaResponseList = commercialService.getCommercialAreasByAdministrationCode(
            administrationCode);
        return ResponseEntity.ok().body(Message.success(areaResponseList));
    }

    @Operation(
        summary = "해당 상권의 존재하는 업종 목록 조회",
        description = "주어진 상권코드에 대해 해당 상권의 존재하는 업종 목록 데이터를 조회합니다."
    )
    @GetMapping("/service/{commercialCode}")
    public ResponseEntity<Message<List<CommercialServiceResponse>>> getCommercialServiceCodeAndServiceCodeName(
        @PathVariable String commercialCode) {
        List<CommercialServiceResponse> serviceResponseList = commercialService.getServiceByCommercialCode(
            commercialCode);
        return ResponseEntity.ok().body(Message.success(serviceResponseList));
    }

    @Operation(
        summary = "해당 상권의 분기별 유동 인구 조회",
        description = "주어진 상권코드에 대해 해당 분기의 유동 인구 데이터를 조회합니다. 기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/foot-traffic/{commercialCode}")
    public ResponseEntity<Message<CommercialFootTrafficResponse>> getFootTrafficByCommercialCodeAndPeriod(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode) {

        CommercialFootTrafficResponse footTrafficResponse = commercialService.getFootTrafficByPeriodAndCommercialCode(
            periodCode, commercialCode);
        return ResponseEntity.ok().body(Message.success(footTrafficResponse));
    }

    @Operation(
        summary = "해당 상권&업종의 분기별 매출 분석 조회",
        description = "주어진 상권코드 및 서비스코드에 대해 해당 분기의 매출분석 데이터를 조회합니다. 기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/sales/{commercialCode}/{serviceCode}")
    public ResponseEntity<Message<CommercialSalesResponse>> getSalesByPeriodAndCommercialCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode,
        @PathVariable String serviceCode) {
        CommercialSalesResponse salesResponse = commercialService.getSalesByPeriodAndCommercialCodeAndServiceCode(
            periodCode, commercialCode, serviceCode);
        return ResponseEntity.ok().body(Message.success(salesResponse));
    }

    @Operation(
        summary = "해당 자치구, 행정동, 상권의 업종 분기별 매출 총 금액 분석",
        description = "주어진 자치구, 행정동, 상권 코드 및 서비스코드에 대해 해당 분기의 매출 총 금액 데이터를 조회합니다. " +
            "기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/sales/{districtCode}/{administrationCode}/{commercialCode}/{serviceCode}")
    public ResponseEntity<Message<AllSalesResponse>> getAllSalesByPeriodAndDistrictCodeAndAdministrationCodeAndCommercialCodeAndServiceCode(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String districtCode,
        @PathVariable String administrationCode,
        @PathVariable String commercialCode,
        @PathVariable String serviceCode) {
        Long memberId = Optional.ofNullable(loginActive).map(MemberLoginActive::id).orElse(0L);
        AllSalesResponse allSalesResponse = commercialService.getAllSalesByPeriodAndDistrictCodeAndAdministrationCodeAndCommercialCodeAndServiceCode(
            memberId, periodCode, districtCode, administrationCode, commercialCode, serviceCode);
        return ResponseEntity.ok().body(Message.success(allSalesResponse));
    }

    @Operation(
        summary = "해당 상권의 분기별 집객 시설 조회",
        description = "주어진 상권코드에 대해 해당 분기의 집객 시설 데이터를 조회합니다. 기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/facility/{commercialCode}")
    public ResponseEntity<Message<CommercialFacilityResponse>> getFacilityByPeriodAndCommercialCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode) {
        CommercialFacilityResponse facilityResponse = commercialService.getFacilityByPeriodAndCommercialCode(
            periodCode, commercialCode);
        return ResponseEntity.ok().body(Message.success(facilityResponse));
    }

    @Operation(
        summary = "해당 상권의 분기별 상주 인구 조회",
        description = "주어진 상권코드에 대해 해당 분기의 상주 인구 데이터를 조회합니다. 기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/population/{commercialCode}")
    public ResponseEntity<Message<CommercialPopulationResponse>> getPopulationByPeriodAndCommercialCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode) {
        CommercialPopulationResponse populationResponse = commercialService.getPopulationByPeriodAndCommercialCode(
            periodCode, commercialCode);
        return ResponseEntity.ok().body(Message.success(populationResponse));
    }

    @Operation(
        summary = "해당 상권이 속한 행정동 정보 조회",
        description = "해당 상권이 속한 행정동의 코드와 이름을 반환하는 기능입니다."
    )
    @GetMapping("/{commercialCode}")
    public ResponseEntity<Message<CommercialAdministrationAreaResponse>> getAdministration(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @PathVariable String commercialCode) {
        Long memberId = Optional.ofNullable(loginActive).map(MemberLoginActive::id).orElse(0L);
        CommercialAdministrationAreaResponse administrationResponse = commercialService.getAdministrationInfoByCommercialCode(
            memberId, commercialCode);
        return ResponseEntity.ok().body(Message.success(administrationResponse));
    }

    @Operation(
        summary = "해당 상권 & 업종의 분기별 점포 분석 조회",
        description = "주어진 상권코드 및 서비스코드에 대해 해당 분기의 점포 분석 데이터를 조회합니다. " +
            "기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/store/{commercialCode}/{serviceCode}")
    public ResponseEntity<Message<CommercialStoreResponse>> getStoreByPeriodAndCommercialCodeAndServiceCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode,
        @PathVariable String serviceCode) {
        CommercialStoreResponse storeResponse = commercialService.getStoreByPeriodAndCommercialCodeAndServiceCode(
            periodCode, commercialCode, serviceCode);
        return ResponseEntity.ok().body(Message.success(storeResponse));
    }

    @Operation(
        summary = "해당 상권의 분기별 지출 내역 분석 조회",
        description = "주어진 상권코드에 대해 해당 분기의 지출 내역 분석 데이터를 조회합니다. " +
            "기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/income/{commercialCode}")
    public ResponseEntity<Message<CommercialIncomeResponse>> getIncomeByPeriodCodeAndCommercialCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String commercialCode) {
        CommercialIncomeResponse incomeResponse = commercialService.getIncomeByPeriodCodeAndCommercialCode(
            periodCode, commercialCode);
        return ResponseEntity.ok().body(Message.success(incomeResponse));
    }

    @Operation(
        summary = "해당 자치구, 행정동, 상권의 분기별 지출 총 금액 분석",
        description = "주어진 자치구, 행정동, 상권 코드에 대해 해당 분기의 지출 촘 금액 데이터를 조회합니다. " +
            "기준년분기코드가 주어지지 않으면 2023년 3분기의 데이터를 사용합니다."
    )
    @GetMapping("/income/{districtCode}/{administrationCode}/{commercialCode}")
    public ResponseEntity<Message<AllIncomeResponse>> getAllIncomeByPeriodCodeAndDistrictCodeAndAdministrationCodeAndCommercialCode(
        @RequestParam(defaultValue = "20233") String periodCode,
        @PathVariable String districtCode,
        @PathVariable String administrationCode,
        @PathVariable String commercialCode) {
        AllIncomeResponse allIncomeResponse = commercialService.getAllIncomeByPeriodCodeAndDistrictCodeAndAdministrationCodeAndCommercialCode(
            periodCode, districtCode, administrationCode, commercialCode);
        return ResponseEntity.ok().body(Message.success(allIncomeResponse));
    }

    @Operation(
        summary = "상권 분석 저장 (북마크)",
        description = "상권분석 조회 한 데이터를 나의 북마크로 저장합니다."
    )
    @PostMapping("/analysis")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> saveAnalysis(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestBody CommercialAnalysisSaveRequest saveRequest) {
        commercialService.saveAnalysis(loginActive.id(), saveRequest);
        return ResponseEntity.ok().body(Message.success());
    }

    @Operation(
        summary = "나의 상권 분석 리스트 조회",
        description = "마이페이지에서 나의 상권 분석 리스트를 조회하는 기능입니다. 페이지네이션을 적용하였습니다."
    )
    @GetMapping("/analysis-list")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<PageResponse<CommercialAnalysisResponse>>> getMyAnalysisListByMemberId(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        PageResponse<CommercialAnalysisResponse> analysisResponsePage = commercialService.getMyAnalysisListByMemberId(
            loginActive.id(), page, size);
        return ResponseEntity.ok().body(Message.success(analysisResponsePage));
    }
}
