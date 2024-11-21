package com.ssafy.backend.domain.commercial.dto.response;

import lombok.Builder;

@Builder
public record CommercialAreaResponse(
    String commercialCode,
    String commercialCodeName,
    String commercialClassificationCode,
    String commercialClassificationCodeName,
    double centerLat,
    double centerLng
) {

}
