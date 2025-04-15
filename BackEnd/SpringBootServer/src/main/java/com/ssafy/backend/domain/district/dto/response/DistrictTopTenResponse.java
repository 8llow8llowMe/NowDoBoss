package com.ssafy.backend.domain.district.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record DistrictTopTenResponse(
    List<FootTrafficDistrictTopTenResponse> footTrafficTopTenList,
    List<SalesDistrictTopTenResponse> salesTopTenList,
    List<OpenedStoreDistrictTopTenResponse> openedRateTopTenList,
    List<ClosedStoreDistrictTopTenResponse> closedRateTopTenList
) {

}
