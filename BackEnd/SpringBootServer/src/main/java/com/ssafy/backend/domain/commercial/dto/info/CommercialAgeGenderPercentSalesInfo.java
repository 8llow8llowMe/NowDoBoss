package com.ssafy.backend.domain.commercial.dto.info;

import lombok.Builder;

/**
 * 연령대별 및 성별별 유동 매출액 비율 정보를 나타내는 record입니다. 각 연령대별로 남성과 여성의 매출액 비율을 포함합니다.
 */
@Builder
public record CommercialAgeGenderPercentSalesInfo(
    double maleTeenSalesPercent,   // 10대 남성의 매출액 비율
    double femaleTeenSalesPercent, // 10대 여성의 매출액 비율
    double maleTwentySalesPercent, // 20대 남성의 매출액 비율
    double femaleTwentySalesPercent, // 20대 여성의 매출액 비율
    double maleThirtySalesPercent, // 30대 남성의 매출액 비율
    double femaleThirtySalesPercent, // 30대 여성의 매출액 비율
    double maleFortySalesPercent,  // 40대 남성의 매출액 비율
    double femaleFortySalesPercent, // 40대 여성의 매출액 비율
    double maleFiftySalesPercent,  // 50대 남성의 매출액 비율
    double femaleFiftySalesPercent, // 50대 여성의 매출액 비율
    double maleSixtySalesPercent, // 60대 이상 남성의 매출액 비율
    double femaleSixtySalesPercent // 60대 이상 여성의 매출액 비율
) {

}
