package com.ssafy.backend.domain.district.dto.response;

import lombok.Builder;

@Builder
public record OpenedStoreDistrictTopTenResponse(
    String districtCode,
    String districtCodeName,
    Double total,
    Double totalRate,
    int level
) {

}
