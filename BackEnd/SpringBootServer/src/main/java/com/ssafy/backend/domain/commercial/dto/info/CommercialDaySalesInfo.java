package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialDaySalesInfo(
    Long monSales,    // 월요일 매출액
    Long tueSales,    // 화요일 매출액
    Long wedSales,    // 수요일 매출액
    Long thuSales,    // 목요일 매출액
    Long friSales,    // 금요일 매출액
    Long satSales,    // 토요일 매출액
    Long sunSales    // 일요일 매출액
) {

}
