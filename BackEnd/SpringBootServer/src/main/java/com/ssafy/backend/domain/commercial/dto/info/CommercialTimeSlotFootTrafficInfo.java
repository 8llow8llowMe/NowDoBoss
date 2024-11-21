package com.ssafy.backend.domain.commercial.dto.info;

/**
 * 시간대별 유동 인구 정보를 나타내는 record입니다. 각 시간대별로 인구 수를 나타내며, 각 필드는 특정 시간대의 유동 인구 수를 포함합니다.
 */
public record CommercialTimeSlotFootTrafficInfo(
    long footTraffic00, // 새벽 시간대 (00:00 ~ 06:00)의 유동 인구 수
    long footTraffic06, // 아침 시간대 (06:00 ~ 11:00)의 유동 인구 수
    long footTraffic11, // 점심 시간대 (11:00 ~ 14:00)의 유동 인구 수
    long footTraffic14, // 오후 시간대 (14:00 ~ 17:00)의 유동 인구 수
    long footTraffic17, // 저녁 시간대 (17:00 ~ 21:00)의 유동 인구 수
    long footTraffic21  // 밤 시간대 (21:00 ~ 24:00)의 유동 인구 수
) {

}
