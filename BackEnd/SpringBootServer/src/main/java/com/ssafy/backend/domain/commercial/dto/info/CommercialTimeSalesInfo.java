package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialTimeSalesInfo(
    long sales00, // 새벽 시간대 (00:00 ~ 06:00) 매출액
    long sales06, // 아침 시간대 (06:00 ~ 11:00) 매출액
    long sales11, // 점심 시간대 (11:00 ~ 14:00) 매출액
    long sales14, // 오후 시간대 (14:00 ~ 17:00) 매출액
    long sales17, // 저녁 시간대 (17:00 ~ 21:00) 매출액
    long sales21  // 밤 시간대 (21:00 ~ 24:00) 매출액
) {

}
