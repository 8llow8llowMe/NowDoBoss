package com.ssafy.backend.domain.commercial.dto.info;

/**
 * 연령대별 유동 인구 정보를 나타내는 record입니다. 각 연령대별로 인구 수를 나타내며, 각 필드는 특정 연령대의 유동 인구 수를 포함합니다.
 */
public record CommercialAgeGroupFootTrafficInfo(
    long teenFootTraffic,   // 10대의 유동 인구 수
    long twentyFootTraffic, // 20대의 유동 인구 수
    long thirtyFootTraffic, // 30대의 유동 인구 수
    long fortyFootTraffic,  // 40대의 유동 인구 수
    long fiftyFootTraffic,  // 50대의 유동 인구 수
    long sixtyFootTraffic   // 60대 이상의 유동 인구 수
) {

}
