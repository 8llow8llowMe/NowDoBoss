package com.ssafy.backend.domain.commercial.dto.info;


import lombok.Builder;

/**
 * 연령대별 및 성별별 유동 인구 비율 정보를 나타내는 record입니다. 각 연령대별로 남성과 여성의 유동 인구 비율을 포함합니다.
 */
@Builder
public record CommercialAgeGenderPercentFootTrafficInfo(
    Double maleTeenFootTrafficPercent,   // 10대 남성의 유동 인구 비율
    Double femaleTeenFootTrafficPercent, // 10대 여성의 유동 인구 비율
    Double maleTwentyFootTrafficPercent, // 20대 남성의 유동 인구 비율
    Double femaleTwentyFootTrafficPercent, // 20대 여성의 유동 인구 비율
    Double maleThirtyFootTrafficPercent, // 30대 남성의 유동 인구 비율
    Double femaleThirtyFootTrafficPercent, // 30대 여성의 유동 인구 비율
    Double maleFortyFootTrafficPercent,  // 40대 남성의 유동 인구 비율
    Double femaleFortyFootTrafficPercent, // 40대 여성의 유동 인구 비율
    Double maleFiftyFootTrafficPercent,  // 50대 남성의 유동 인구 비율
    Double femaleFiftyFootTrafficPercent, // 50대 여성의 유동 인구 비율
    Double maleSixtyFootTrafficPercent, // 60대 이상 남성의 유동 인구 비율
    Double femaleSixtyFootTrafficPercent // 60대 이상 여성의 유동 인구 비율
) {

}
