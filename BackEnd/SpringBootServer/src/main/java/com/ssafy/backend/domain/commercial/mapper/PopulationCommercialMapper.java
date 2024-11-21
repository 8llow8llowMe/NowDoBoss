package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialPopulationInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialPopulationResponse;
import com.ssafy.backend.domain.commercial.entity.PopulationCommercial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PopulationCommercialMapper {

    // populationCommercial -> CommercialPopulationInfo 매핑
    CommercialPopulationInfo entityToPopulationInfo(PopulationCommercial populationCommercial);

    default CommercialPopulationResponse toCommercialPopulationResponse(
        PopulationCommercial populationCommercial) {

        // 남자 여자 인구 비율 계산 -> 소수점 첫째자리까지
        double malePercentage = Math.round((double) populationCommercial.getMalePopulation()
            / populationCommercial.getTotalPopulation() * 1000) / 10.0;
        double femalePercentage = Math.round((double) populationCommercial.getFemalePopulation()
            / populationCommercial.getTotalPopulation() * 1000) / 10.0;

        return CommercialPopulationResponse.builder()
            .populationInfo(entityToPopulationInfo(populationCommercial))
            .malePercentage(malePercentage)
            .femalePercentage(femalePercentage)
            .build();
    }
}
