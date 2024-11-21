package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialDaySalesCountInfo(
    long monSalesCount,
    long tueSalesCount,
    long wedSalesCount,
    long thuSalesCount,
    long friSalesCount,
    long satSalesCount,
    long sunSalesCount
) {

}
