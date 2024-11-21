package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialTypeIncomeInfo(
    long groceryPrice,
    long clothesPrice,
    long medicalPrice,
    long lifePrice,
    long trafficPrice,
    long leisurePrice,
    long culturePrice,
    long educationPrice,
    long luxuryPrice
) {

}
