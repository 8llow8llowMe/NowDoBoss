package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.administration.dto.info.AdministrationTotalIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTotalIncomeInfo;
import com.ssafy.backend.domain.district.dto.info.DistrictTotalIncomeInfo;
import lombok.Builder;

@Builder
public record AllIncomeResponse(
    DistrictTotalIncomeInfo districtTotalIncomeInfo,
    AdministrationTotalIncomeInfo administrationTotalIncomeInfo,
    CommercialTotalIncomeInfo commercialTotalIncomeInfo
) {

}
