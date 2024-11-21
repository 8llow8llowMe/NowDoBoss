package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.commercial.dto.info.CommercialPopulationInfo;
import lombok.Builder;

/**
 * 최종 사용자에게 반환될 상주 인구 응답 DTO입니다.
 */
@Builder
public record CommercialPopulationResponse(
    CommercialPopulationInfo populationInfo,
    Double malePercentage, // 남성 상주인구 퍼센트
    Double femalePercentage // 여성 상주인구 퍼센트
) {

}
