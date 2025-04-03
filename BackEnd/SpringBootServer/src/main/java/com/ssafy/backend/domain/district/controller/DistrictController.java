package com.ssafy.backend.domain.district.controller;

import com.ssafy.backend.domain.administration.dto.info.ClosedStoreAdministrationTopFiveInfo;
import com.ssafy.backend.domain.administration.dto.info.OpenedStoreAdministrationTopFiveInfo;
import com.ssafy.backend.domain.administration.dto.info.SalesAdministrationTopFiveInfo;
import com.ssafy.backend.domain.district.dto.info.SalesDistrictMonthSalesTopFiveInfo;
import com.ssafy.backend.domain.district.dto.info.StoreDistrictTotalTopEightInfo;
import com.ssafy.backend.domain.district.dto.response.ChangeIndicatorDistrictResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictAreaResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictDetailResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.FootTrafficDistrictDetailResponse;
import com.ssafy.backend.domain.district.service.DistrictService;
import com.ssafy.backend.global.annotation.PublicEndpoint;
import com.ssafy.backend.global.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/district")
public class DistrictController {

    private final DistrictService districtService;

    @Operation(
        summary = "자치구 Top 5 리스트",
        description = "유동인구, 매출, 개업률, 폐업률 Top 5 리스트를 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/top/ten")
    public ResponseEntity<Response<DistrictTopTenResponse>> getTopTenDistricts() {
        DistrictTopTenResponse districtTopTenResponse = districtService.getTopTenDistricts();
        return ResponseEntity.ok().body(Response.success(districtTopTenResponse));
    }

    @Operation(
        summary = "특정 자치구 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/{districtCode}")
    public ResponseEntity<Response<DistrictDetailResponse>> getDistrictDetail(
        @PathVariable String districtCode) {
        DistrictDetailResponse districtDetailResponse = districtService.getDistrictDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(districtDetailResponse));
    }

    @Operation(
        summary = "특정 자치구 유동인구 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/footTraffic/{districtCode}")
    public ResponseEntity<Response<FootTrafficDistrictDetailResponse>> getDistrictFootTrafficDetail(
        @PathVariable String districtCode) {
        FootTrafficDistrictDetailResponse footTrafficDistrictDetailResponse = districtService.getDistrictFootTrafficDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(footTrafficDistrictDetailResponse));
    }

    @Operation(
        summary = "특정 자치구 변화 지표 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/changeIndicator/{districtCode}")
    public ResponseEntity<Response<ChangeIndicatorDistrictResponse>> getDistrictChangeDetail(
        @PathVariable String districtCode) {
        ChangeIndicatorDistrictResponse changeIndicatorDistrictResponse = districtService.getDistrictChangeIndicatorDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(changeIndicatorDistrictResponse));
    }

    @Operation(
        summary = "특정 자치구 점포 수 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/store/total/{districtCode}")
    public ResponseEntity<Response<List<StoreDistrictTotalTopEightInfo>>> getDistrictTotalStoreDetail(
        @PathVariable String districtCode) {
        List<StoreDistrictTotalTopEightInfo> storeDistrictTotalTopEightInfoList = districtService.getDistrictTotalStoreDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(storeDistrictTotalTopEightInfoList));
    }

    @Operation(
        summary = "특정 자치구 개업률 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/store/opened/{districtCode}")
    public ResponseEntity<Response<List<OpenedStoreAdministrationTopFiveInfo>>> getDistrictOpenedStoreDetail(
        @PathVariable String districtCode) {
        List<OpenedStoreAdministrationTopFiveInfo> openedStoreAdministrationTopFiveInfoList = districtService.getDistrictOpenedStoreDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(openedStoreAdministrationTopFiveInfoList));
    }

    @Operation(
        summary = "특정 자치구 폐업률 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/store/closed/{districtCode}")
    public ResponseEntity<Response<List<ClosedStoreAdministrationTopFiveInfo>>> getDistrictStoreDetail(
        @PathVariable String districtCode) {
        List<ClosedStoreAdministrationTopFiveInfo> closedStoreAdministrationTopFiveInfoList = districtService.getDistrictClosedStoreDetail(
            districtCode);
        return ResponseEntity.ok().body(Response.success(closedStoreAdministrationTopFiveInfoList));
    }

    @Operation(
        summary = "특정 자치구 업종별 매출 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/sales/service/{districtCode}")
    public ResponseEntity<Response<List<SalesDistrictMonthSalesTopFiveInfo>>> getDistrictSalesDetailByServiceCode(
        @PathVariable String districtCode) {
        List<SalesDistrictMonthSalesTopFiveInfo> salesDistrictMonthSalesTopFiveInfoList = districtService.getDistrictSalesDetailByServiceCode(
            districtCode);
        return ResponseEntity.ok().body(Response.success(salesDistrictMonthSalesTopFiveInfoList));
    }

    @Operation(
        summary = "특정 자치구 해당 행정동 매출 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/detail/sales/administration/{districtCode}")
    public ResponseEntity<Response<List<SalesAdministrationTopFiveInfo>>> getDistrictSalesDetailByAdministrationCode(
        @PathVariable String districtCode) {
        List<SalesAdministrationTopFiveInfo> salesAdministrationTopFiveInfoList = districtService.getDistrictSalesDetailByAdministrationCode(
            districtCode);
        return ResponseEntity.ok().body(Response.success(salesAdministrationTopFiveInfoList));
    }

    @Operation(
        summary = "전체 자치구 목록 조회",
        description = "모든 자치구의 코드와 이름을 반환하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/areas")
    public ResponseEntity<Response<List<DistrictAreaResponse>>> getAllDistricts() {
        List<DistrictAreaResponse> areaResponseList = districtService.getAllDistricts();
        return ResponseEntity.ok().body(Response.success(areaResponseList));
    }
}
