package com.ssafy.backend.domain.district.repository;

import com.ssafy.backend.domain.district.dto.info.StoreDistrictTotalTopEightInfo;
import com.ssafy.backend.domain.district.dto.response.ClosedStoreDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.OpenedStoreDistrictTopTenResponse;
import java.util.List;


public interface StoreDistrictCustomRepository {

    List<OpenedStoreDistrictTopTenResponse> getTopTenOpenedStoreDistrictByPeriodCode();

    List<ClosedStoreDistrictTopTenResponse> getTopTenClosedStoreDistrictByPeriodCode();

    List<StoreDistrictTotalTopEightInfo> getTopEightTotalStoreByServiceCode(String periodCode,
        String districtCode);
}
