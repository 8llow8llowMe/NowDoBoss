package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.commercial.dto.info.CommercialFranchiseStoreInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialOpenAndCloseStoreInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialSameStoreInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record CommercialStoreResponse(
    List<CommercialSameStoreInfo> sameStoreInfos,
    Long sameTotalStore,
    CommercialFranchiseStoreInfo franchiseStoreInfo,
    CommercialOpenAndCloseStoreInfo openAndCloseStoreInfo
) {

}
