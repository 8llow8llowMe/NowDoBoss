package com.ssafy.backend.domain.district.dto.info;

import lombok.Builder;

@Builder
public record StoreDistrictTotalTopEightInfo(
    String serviceCode,
    String serviceCodeName,
    Long totalStore
) {

}
