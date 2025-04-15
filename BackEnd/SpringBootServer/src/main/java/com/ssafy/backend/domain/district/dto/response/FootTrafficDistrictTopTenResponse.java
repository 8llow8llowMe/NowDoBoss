package com.ssafy.backend.domain.district.dto.response;

import lombok.Builder;

@Builder
public record FootTrafficDistrictTopTenResponse(
    String districtCode,
    String districtCodeName,
    Long total,
    Double totalRate,
    int level
) {

}
