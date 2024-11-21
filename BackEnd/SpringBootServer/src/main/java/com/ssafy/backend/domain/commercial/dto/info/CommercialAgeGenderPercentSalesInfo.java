package com.ssafy.backend.domain.commercial.dto.info;

import lombok.Builder;

/**
 * 연령대별 및 성별별 유동 매출액 비율 정보를 나타내는 record입니다. 각 연령대별로 남성과 여성의 매출액 비율을 포함합니다.
 */
@Builder
public record CommercialAgeGenderPercentSalesInfo(
    Double maleTeenSalesPercent,   // 10대 남성의 매출액 비율
    Double femaleTeenSalesPercent, // 10대 여성의 매출액 비율
    Double maleTwentySalesPercent, // 20대 남성의 매출액 비율
    Double femaleTwentySalesPercent, // 20대 여성의 매출액 비율
    Double maleThirtySalesPercent, // 30대 남성의 매출액 비율
    Double femaleThirtySalesPercent, // 30대 여성의 매출액 비율
    Double maleFortySalesPercent,  // 40대 남성의 매출액 비율
    Double femaleFortySalesPercent, // 40대 여성의 매출액 비율
    Double maleFiftySalesPercent,  // 50대 남성의 매출액 비율
    Double femaleFiftySalesPercent, // 50대 여성의 매출액 비율
    Double maleSixtySalesPercent, // 60대 이상 남성의 매출액 비율
    Double femaleSixtySalesPercent // 60대 이상 여성의 매출액 비율
) {

}
