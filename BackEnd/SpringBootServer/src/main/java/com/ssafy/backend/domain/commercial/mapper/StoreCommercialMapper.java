package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialFranchiseStoreInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialOpenAndCloseStoreInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialSameStoreInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialStoreResponse;
import com.ssafy.backend.domain.commercial.entity.StoreCommercial;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreCommercialMapper {

    @Mapping(target = "normalStore", source = "totalStore")
    @Mapping(target = "normalStorePercentage", expression = "java(calculatePercentage(storeCommercial.getTotalStore(), storeCommercial.getTotalStore() + storeCommercial.getFranchiseStore()))")
    @Mapping(target = "franchisePercentage", expression = "java(calculatePercentage(storeCommercial.getFranchiseStore(), storeCommercial.getTotalStore() + storeCommercial.getFranchiseStore()))")
    CommercialFranchiseStoreInfo entityToFranchiseStoreInfo(StoreCommercial storeCommercial);

    CommercialOpenAndCloseStoreInfo entityToOpenAndCloseStoreInfo(StoreCommercial storeCommercial);

    CommercialSameStoreInfo entityToSameStoreInfo(StoreCommercial storeCommercial);

    default long calculateTotalStores(List<CommercialSameStoreInfo> sameStoreInfos) {

        return sameStoreInfos.stream()
            .mapToLong(CommercialSameStoreInfo::totalStore)
            .sum();
    }

    default double calculatePercentage(long part, long total) {
        return total > 0 ? Math.round((double) part / total * 100.0 * 100.0) / 100.0 : 0.0;
    }

    default CommercialStoreResponse toCommercialStoreResponse(
        List<CommercialSameStoreInfo> sameStoreInfos, StoreCommercial storeCommercial) {

        long sameTotalStore = calculateTotalStores(sameStoreInfos);

        return CommercialStoreResponse.builder()
            .sameStoreInfos(sameStoreInfos)
            .sameTotalStore(sameTotalStore)
            .franchiseStoreInfo(entityToFranchiseStoreInfo(storeCommercial))
            .openAndCloseStoreInfo(entityToOpenAndCloseStoreInfo(storeCommercial))
            .build();
    }
}
