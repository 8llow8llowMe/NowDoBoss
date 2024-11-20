package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGroupFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDayOfWeekFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialGenderSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSlotFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
import com.ssafy.backend.domain.commercial.entity.FootTrafficCommercial;
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

    @Mapping(target = "periodCode", source = "periodCode")
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

}
