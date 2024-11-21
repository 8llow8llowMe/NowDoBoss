package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialFranchiseStoreInfo(
    long normalStore,
    long franchiseStore,
    double normalStorePercentage,
    double franchisePercentage
) {

}
