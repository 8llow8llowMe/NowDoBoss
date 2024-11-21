package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.administration.dto.info.AdministrationTotalSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTotalSalesInfo;
import com.ssafy.backend.domain.district.dto.info.DistrictTotalSalesInfo;
import lombok.Builder;

@Builder
public record AllSalesResponse(
    DistrictTotalSalesInfo districtTotalSalesInfo,
    AdministrationTotalSalesInfo administrationTotalSalesInfo,
    CommercialTotalSalesInfo commercialTotalSalesInfo
) {

}
