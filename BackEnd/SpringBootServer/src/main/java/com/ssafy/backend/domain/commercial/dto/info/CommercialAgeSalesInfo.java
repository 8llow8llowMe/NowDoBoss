package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialAgeSalesInfo(
    long teenSales,   // 10대 매출액
    long twentySales, // 20대 매출액
    long thirtySales, // 30대 매출액
    long fortySales,  // 40대 매출액
    long fiftySales,  // 50대 매출액
    long sixtySales   // 60대 이상 매출액
) {

}
