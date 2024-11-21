package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAvgIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTypeIncomeInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record CommercialIncomeResponse(
    CommercialAvgIncomeInfo avgIncomeInfo,
    List<CommercialAnnualQuarterIncomeInfo> annualQuarterIncomeInfos,
    CommercialTypeIncomeInfo typeIncomeInfo
) {

}
