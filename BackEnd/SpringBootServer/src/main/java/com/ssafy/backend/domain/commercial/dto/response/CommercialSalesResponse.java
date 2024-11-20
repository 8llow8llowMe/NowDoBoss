package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDaySalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialGenderSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesCountInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSalesInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record CommercialSalesResponse(
    CommercialTimeSalesInfo timeSalesInfo,
    CommercialDaySalesInfo daySalesInfo,
    CommercialAgeSalesInfo ageSalesInfo,
    CommercialAgeGenderPercentSalesInfo ageGenderPercentSales,
    CommercialDaySalesCountInfo daySalesCountInfo,
    CommercialTimeSalesCountInfo timeSalesCountInfo,
    CommercialGenderSalesCountInfo genderSalesCountInfo,
    List<CommercialAnnualQuarterSalesInfo> annualQuarterSalesInfos
) {

}
