package com.ssafy.backend.domain.commercial.dto.info;

/**
 * 요일별 유동 인구 정보를 나타내는 record입니다. 각 요일별로 인구 수를 나타내며, 각 필드는 특정 요일의 유동 인구 수를 포함합니다.
 */
public record CommercialDayOfWeekFootTrafficInfo(
    long monFootTraffic,    // 월요일의 유동 인구 수
    long tueFootTraffic,    // 화요일의 유동 인구 수
    long wedFootTraffic,    // 수요일의 유동 인구 수
    long thuFootTraffic,    // 목요일의 유동 인구 수
    long friFootTraffic,    // 금요일의 유동 인구 수
    long satFootTraffic,    // 토요일의 유동 인구 수
    long sunFootTraffic // 일요일의 유동 인구 수
) {

}
