package com.ssafy.backend.domain.commercial.dto.info;

public record CommercialFranchiseeStoreInfo(
    long normalStore,
    long franchiseeStore,
    double normalStorePercentage,
    double franchiseePercentage
) {

}
