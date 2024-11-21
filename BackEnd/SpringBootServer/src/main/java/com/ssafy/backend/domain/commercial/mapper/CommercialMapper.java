package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialPopulationInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialSchoolInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFacilityResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialPopulationResponse;
import com.ssafy.backend.domain.commercial.entity.FacilityCommercial;
import com.ssafy.backend.domain.commercial.entity.PopulationCommercial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommercialMapper {

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

    // FacilityCommercial -> CommercialSchoolInfo 매핑
    default CommercialSchoolInfo entityToShcoolInfo(FacilityCommercial facilityCommercial) {

        // 학교 수 합산 처리
        long totalSchoolsCount = facilityCommercial.getElementarySchoolCnt()
            + facilityCommercial.getMiddleSchoolCnt()
            + facilityCommercial.getHighSchoolCnt();

        return CommercialSchoolInfo.builder()
            .totalSchoolsCount(totalSchoolsCount)
            .universityCount(facilityCommercial.getUniversityCnt())
            .build();
    }

    default CommercialFacilityResponse toCommercialFacilityResponse(
        FacilityCommercial facilityCommercial) {

        long facilityCnt = facilityCommercial.getFacilityCnt();
        long totalTransportCnt =
            facilityCommercial.getSubwayStationCnt() + facilityCommercial.getBusStopCnt();

        return CommercialFacilityResponse.builder()
            .facilityCnt(facilityCnt)
            .commercialSchoolInfo(entityToShcoolInfo(facilityCommercial))
            .totalTransportCnt(totalTransportCnt)
            .build();
    }

}
