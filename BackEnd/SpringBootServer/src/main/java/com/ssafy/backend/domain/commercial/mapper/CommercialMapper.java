package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialGenderSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
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

    CommercialGenderSalesCountInfo entityToGenderSalesInfo(SalesCommercial salesCommercial);

    @Mapping(target = "periodCode", source = "periodCode")
    @Mapping(target = "totalSales", source = "monthSales")
    CommercialAnnualQuarterSalesInfo entityToAnnualQuarterSalesInfo(
        SalesCommercial salesCommercial);

    /**
     * 기본 메서드로 모든 변환 로직을 한 번에 처리
     */
    default CommercialSalesResponse toCommercialSalesResponse(SalesCommercial salesCommercial,
        List<SalesCommercial> quarterlySales,
        CommercialAgeGenderPercentSalesInfo ageGenderPercentSales) {

        return new CommercialSalesResponse(
            entityToTimeSalesInfo(salesCommercial),
            entityToDaySalesInfo(salesCommercial),
            entityToAgeSalesInfo(salesCommercial),
            ageGenderPercentSales,
            entityToDaySalesCountInfo(salesCommercial),
            entityToTimeSalesCountInfo(salesCommercial),
            entityToGenderSalesInfo(salesCommercial),
            quarterlySales.stream()
                .map(this::entityToAnnualQuarterSalesInfo) // `toAnnualQuarterSalesInfo`를 이용한 매핑
                .toList()
        );
    }
}
