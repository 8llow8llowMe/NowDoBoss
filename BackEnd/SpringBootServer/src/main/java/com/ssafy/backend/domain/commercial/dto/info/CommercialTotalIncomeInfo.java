package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialTotalIncomeInfo(
    String commercialCode,
    String commercialCodeName,
    long totalPrice
) {

}
