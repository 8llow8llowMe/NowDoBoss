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
import com.ssafy.backend.global.common.dto.Message;
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
    @GetMapping("/top/ten")
    public ResponseEntity<Message<DistrictTopTenResponse>> getTopTenDistricts() {
        DistrictTopTenResponse districtTopTenResponse = districtService.getTopTenDistricts();
        return ResponseEntity.ok().body(Message.success(districtTopTenResponse));
    }

    @Operation(
        summary = "특정 자치구 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/{districtCode}")
    public ResponseEntity<Message<DistrictDetailResponse>> getDistrictDetail(
        @PathVariable String districtCode) {
        DistrictDetailResponse districtDetailResponse = districtService.getDistrictDetail(
            districtCode);
        return ResponseEntity.ok().body(Message.success(districtDetailResponse));
    }

    @Operation(
        summary = "특정 자치구 유동인구 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/footTraffic/{districtCode}")
    public ResponseEntity<Message<FootTrafficDistrictDetailResponse>> getDistrictFootTrafficDetail(
        @PathVariable String districtCode) {
        FootTrafficDistrictDetailResponse footTrafficDistrictDetailResponse = districtService.getDistrictFootTrafficDetail(
            districtCode);
        return ResponseEntity.ok().body(Message.success(footTrafficDistrictDetailResponse));
    }

    @Operation(
        summary = "특정 자치구 변화 지표 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/changeIndicator/{districtCode}")
    public ResponseEntity<Message<ChangeIndicatorDistrictResponse>> getDistrictChangeDetail(
        @PathVariable String districtCode) {
        ChangeIndicatorDistrictResponse changeIndicatorDistrictResponse = districtService.getDistrictChangeIndicatorDetail(
            districtCode);
        return ResponseEntity.ok().body(Message.success(changeIndicatorDistrictResponse));
    }

    @Operation(
        summary = "특정 자치구 점포 수 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/store/total/{districtCode}")
    public ResponseEntity<Message<List<StoreDistrictTotalTopEightInfo>>> getDistrictTotalStoreDetail(
        @PathVariable String districtCode) {
        List<StoreDistrictTotalTopEightInfo> storeDistrictTotalTopEightInfoList = districtService.getDistrictTotalStoreDetail(
            districtCode);
        return ResponseEntity.ok().body(Message.success(storeDistrictTotalTopEightInfoList));
    }

    @Operation(
        summary = "특정 자치구 개업률 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/store/opened/{districtCode}")
    public ResponseEntity<Message<List<OpenedStoreAdministrationTopFiveInfo>>> getDistrictOpenedStoreDetail(
        @PathVariable String districtCode) {
        List<OpenedStoreAdministrationTopFiveInfo> openedStoreAdministrationTopFiveInfoList = districtService.getDistrictOpenedStoreDetail(
            districtCode);
        return ResponseEntity.ok().body(Message.success(openedStoreAdministrationTopFiveInfoList)));
    }

    @Operation(
        summary = "특정 자치구 폐업률 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/store/closed/{districtCode}")
    public ResponseEntity<Message<List<ClosedStoreAdministrationTopFiveInfo>>> getDistrictStoreDetail(
        @PathVariable String districtCode) {
        System.out.println("해당 자치구 상세 분석 가져오기!");
        return ResponseEntity.ok()
            .body(Message.success(districtService.getDistrictClosedStoreDetail(districtCode)));
    }

    @Operation(
        summary = "특정 자치구 업종별 매출 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/sales/service/{districtCode}")
    public ResponseEntity<Message<List<SalesDistrictMonthSalesTopFiveInfo>>> getDistrictSalesDetailByServiceCode(
        @PathVariable String districtCode) {

        return ResponseEntity.ok().body(
            Message.success(districtService.getDistrictSalesDetailByServiceCode(districtCode)));
    }

    @Operation(
        summary = "특정 자치구 해당 행정동 매출 상세 분석",
        description = "상권변화지표 상세, 유동인구 상세, 점포 상세 분석을 제공하는 기능입니다."
    )
    @GetMapping("/detail/sales/administration/{districtCode}")
    public ResponseEntity<Message<List<SalesAdministrationTopFiveInfo>>> getDistrictSalesDetailByAdministrationCode(
        @PathVariable String districtCode) {
        System.out.println("해당 자치구 상세 분석 가져오기!");
        return ResponseEntity.ok().body(Message.success(
            districtService.getDistrictSalesDetailByAdministrationCode(districtCode)));
    }

    @Operation(
        summary = "전체 자치구 목록 조회",
        description = "모든 자치구의 코드와 이름을 반환하는 기능입니다."
    )
    @GetMapping("/areas")
    public ResponseEntity<Message<List<DistrictAreaResponse>>> getAllDistricts() {
        List<DistrictAreaResponse> areaResponseList = districtService.getAllDistricts();
        return ResponseEntity.ok().body(Message.success(areaResponseList));
    }
}
