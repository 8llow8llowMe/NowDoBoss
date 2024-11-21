package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGroupFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDayOfWeekFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialGenderSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialPopulationInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialSchoolInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSlotFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFacilityResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFootTrafficResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialPopulationResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
import com.ssafy.backend.domain.commercial.entity.FacilityCommercial;
import com.ssafy.backend.domain.commercial.entity.FootTrafficCommercial;
import com.ssafy.backend.domain.commercial.entity.PopulationCommercial;
import com.ssafy.backend.domain.commercial.entity.SalesCommercial;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommercialMapper {

    // SalesCommercial -> 각 DTO 매핑
    CommercialTimeSalesInfo entityToTimeSalesInfo(SalesCommercial salesCommercial);

    CommercialDaySalesInfo entityToDaySalesInfo(SalesCommercial salesCommercial);

    CommercialAgeSalesInfo entityToAgeSalesInfo(SalesCommercial salesCommercial);

    CommercialDaySalesCountInfo entityToDaySalesCountInfo(SalesCommercial salesCommercial);

    CommercialTimeSalesCountInfo entityToTimeSalesCountInfo(SalesCommercial salesCommercial);

    CommercialGenderSalesCountInfo entityToGenderSalesCountInfo(SalesCommercial salesCommercial);

    // FootTrafficCommercial -> 각 DTO 매핑
    CommercialTimeSlotFootTrafficInfo entityToTimeSlotFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    CommercialDayOfWeekFootTrafficInfo entityToDayOfWeekFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    CommercialAgeGroupFootTrafficInfo entityToAgeGroupFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    // populationCommercial -> CommercialPopulationInfo 매핑
    CommercialPopulationInfo entityToPopulationInfo(PopulationCommercial populationCommercial);

    @Mapping(target = "totalSales", source = "monthSales")
    CommercialAnnualQuarterSalesInfo entityToAnnualQuarterSalesInfo(
        SalesCommercial salesCommercial);

    default CommercialSalesResponse toCommercialSalesResponse(SalesCommercial salesCommercial,
        List<SalesCommercial> quarterlySales,
        CommercialAgeGenderPercentSalesInfo ageGenderPercentSales) {

        return CommercialSalesResponse.builder()
            .timeSalesInfo(entityToTimeSalesInfo(salesCommercial))
            .daySalesInfo(entityToDaySalesInfo(salesCommercial))
            .ageSalesInfo(entityToAgeSalesInfo(salesCommercial))
            .ageGenderPercentSales(ageGenderPercentSales)
            .daySalesCountInfo(entityToDaySalesCountInfo(salesCommercial))
            .timeSalesCountInfo(entityToTimeSalesCountInfo(salesCommercial))
            .genderSalesCountInfo(entityToGenderSalesCountInfo(salesCommercial))
            .annualQuarterSalesInfos(quarterlySales.stream()
                .map(this::entityToAnnualQuarterSalesInfo)
                .toList())
            .build();
    }

    default CommercialFootTrafficResponse toCommercialFootTrafficResponse(
        FootTrafficCommercial footTrafficCommercial,
        CommercialAgeGenderPercentFootTrafficInfo ageGenderPercentFootTrafficInfo) {

        return CommercialFootTrafficResponse.builder()
            .timeSlotFootTraffic(entityToTimeSlotFootTrafficInfo(footTrafficCommercial))
            .dayOfWeekFootTraffic(entityToDayOfWeekFootTrafficInfo(footTrafficCommercial))
            .ageGroupFootTraffic(entityToAgeGroupFootTrafficInfo(footTrafficCommercial))
            .ageGenderPercentFootTraffic(ageGenderPercentFootTrafficInfo)
            .build();
    }

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
