package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.commercial.dto.info.CommercialSchoolInfo;
import lombok.Builder;

@Builder
public record CommercialFacilityResponse(
    Long facilityCnt,   // 총 집객 시설 수
    CommercialSchoolInfo commercialSchoolInfo,  // 해당 상권 내의 학교 수 정보
    Long totalTransportCnt  // 총 교통 시설 수
) {

}
