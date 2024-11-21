package com.ssafy.backend.domain.commercial.dto.info;

/**
 * 상권별 상주 인구 정보를 담는 DTO입니다.
 */
public record CommercialPopulationInfo(
    long totalPopulation,
    long teenPopulation,
    long twentyPopulation,
    long thirtyPopulation,
    long fortyPopulation,
    long fiftyPopulation,
    long sixtyPopulation
) {

}
