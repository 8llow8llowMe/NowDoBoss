package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialDaySalesInfo(
    long monSales,    // 월요일 매출액
    long tueSales,    // 화요일 매출액
    long wedSales,    // 수요일 매출액
    long thuSales,    // 목요일 매출액
    long friSales,    // 금요일 매출액
    long satSales,    // 토요일 매출액
    long sunSales    // 일요일 매출액
) {

}
