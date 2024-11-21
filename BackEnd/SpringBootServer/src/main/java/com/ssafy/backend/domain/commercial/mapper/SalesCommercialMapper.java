package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.administration.entity.SalesAdministration;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialGenderSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.response.AllSalesResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
import com.ssafy.backend.domain.commercial.entity.SalesCommercial;
import com.ssafy.backend.domain.district.entity.SalesDistrict;
import com.ssafy.backend.global.util.PercentCalculator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesCommercialMapper {

    // SalesCommercial -> 각 DTO 매핑
    CommercialTimeSalesInfo entityToTimeSalesInfo(SalesCommercial salesCommercial);

    CommercialDaySalesInfo entityToDaySalesInfo(SalesCommercial salesCommercial);

    CommercialAgeSalesInfo entityToAgeSalesInfo(SalesCommercial salesCommercial);

    CommercialDaySalesCountInfo entityToDaySalesCountInfo(SalesCommercial salesCommercial);

    CommercialTimeSalesCountInfo entityToTimeSalesCountInfo(SalesCommercial salesCommercial);

    CommercialGenderSalesCountInfo entityToGenderSalesCountInfo(SalesCommercial salesCommercial);

    @Mapping(target = "totalSales", source = "monthSales")
    CommercialAnnualQuarterSalesInfo entityToAnnualQuarterSalesInfo(
        SalesCommercial salesCommercial);

    @Mapping(target = "districtTotalSalesInfo.districtCode", source = "salesDistrict.districtCode")
    @Mapping(target = "districtTotalSalesInfo.districtCodeName", source = "salesDistrict.districtCodeName")
    @Mapping(target = "districtTotalSalesInfo.totalSales", source = "salesDistrict.monthSales")
    @Mapping(target = "administrationTotalSalesInfo.administrationCode", source = "salesAdministration.administrationCode")
    @Mapping(target = "administrationTotalSalesInfo.administrationCodeName", source = "salesAdministration.administrationCodeName")
    @Mapping(target = "administrationTotalSalesInfo.totalSales", source = "salesAdministration.monthSales")
    @Mapping(target = "commercialTotalSalesInfo.commercialCode", source = "salesCommercial.commercialCode")
    @Mapping(target = "commercialTotalSalesInfo.commercialCodeName", source = "salesCommercial.commercialCodeName")
    @Mapping(target = "commercialTotalSalesInfo.totalSales", source = "salesCommercial.monthSales")
    AllSalesResponse mapToAllSalesResponse(SalesDistrict salesDistrict,
        SalesAdministration salesAdministration, SalesCommercial salesCommercial);

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

    default CommercialAgeGenderPercentSalesInfo toCommercialAgeGenderPercentSalesInfo(
        SalesCommercial salesCommercial) {

        long total = salesCommercial.getMaleSales() + salesCommercial.getFemaleSales();

        return CommercialAgeGenderPercentSalesInfo.builder()
            .maleTeenSalesPercent( // 10대 남자
                PercentCalculator.calculatePercent(salesCommercial.getTeenSales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleTeenSalesPercent( // 10대 여자
                PercentCalculator.calculatePercent(salesCommercial.getTeenSales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .maleTwentySalesPercent( // 20대 남자
                PercentCalculator.calculatePercent(salesCommercial.getTwentySales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleTwentySalesPercent( // 20대 여자
                PercentCalculator.calculatePercent(salesCommercial.getTwentySales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .maleThirtySalesPercent( // 30대 남자
                PercentCalculator.calculatePercent(salesCommercial.getThirtySales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleThirtySalesPercent( // 30대 여자
                PercentCalculator.calculatePercent(salesCommercial.getThirtySales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .maleFortySalesPercent( // 40대 남자
                PercentCalculator.calculatePercent(salesCommercial.getFortySales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleFortySalesPercent( // 40대 여자
                PercentCalculator.calculatePercent(salesCommercial.getFortySales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .maleFiftySalesPercent( // 50대 남자
                PercentCalculator.calculatePercent(salesCommercial.getFiftySales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleFiftySalesPercent( // 50대 여자
                PercentCalculator.calculatePercent(salesCommercial.getFiftySales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .maleSixtySalesPercent( // 60대 이상 남자
                PercentCalculator.calculatePercent(salesCommercial.getSixtySales(),
                    salesCommercial.getMaleSales(), total)
            )
            .femaleSixtySalesPercent( // 60대 이상 여자
                PercentCalculator.calculatePercent(salesCommercial.getSixtySales(),
                    salesCommercial.getFemaleSales(), total)
            )
            .build();
    }
}
