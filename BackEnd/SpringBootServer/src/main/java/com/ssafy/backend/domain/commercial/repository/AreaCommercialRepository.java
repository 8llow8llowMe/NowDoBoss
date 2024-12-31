package com.ssafy.backend.domain.commercial.repository;

import com.ssafy.backend.domain.commercial.dto.info.AdministrationInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialInfo;
import com.ssafy.backend.domain.commercial.dto.info.DistrictInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationAreaResponse;
import com.ssafy.backend.domain.commercial.entity.AreaCommercial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaCommercialRepository extends JpaRepository<AreaCommercial, Long> {

    List<AreaCommercial> findAllByDistrictCode(String districtCode);

    List<AreaCommercial> findByAdministrationCode(String administrationCode);

    CommercialAdministrationAreaResponse findByCommercialCode(String commercialCode);

    @Query(value = "SELECT commercial_code_name FROM area_commercial WHERE commercial_code = :commercialCode", nativeQuery = true)
    String findCommercialCodeNameByCommercialCode(String commercialCode);

    @Query(value = "SELECT DISTINCT district_code_name FROM area_commercial WHERE district_code = :districtCode", nativeQuery = true)
    String findDistrictCodeNameByDistrictCode(String districtCode);

    @Query(value = "SELECT DISTINCT administration_code_name FROM area_commercial WHERE administration_code = :administrationCode", nativeQuery = true)
    String findAdministrationCodeNameByAdministrationCode(String administrationCode);


    @Query(value =
        "SELECT DISTINCT new com.ssafy.backend.domain.commercial.dto.info.DistrictInfo(a.districtCode, a.districtCodeName)"
            +
            "from AreaCommercial a " +
            "where a.districtCodeName = :districtCodeName")
    DistrictInfo findDistrictInfoByDistrictCodeName(String districtCodeName);

    @Query(value =
        "SELECT DISTINCT new com.ssafy.backend.domain.commercial.dto.info.AdministrationInfo(a.districtCode, a.districtCodeName, a.administrationCode, a.administrationCodeName)"
            +
            "from AreaCommercial a " +
            "where a.administrationCodeName = :administrationCodeName")
    AdministrationInfo findAdministrationInfoByAdministrationCodeName(
        String administrationCodeName);

    @Query(value =
        "SELECT DISTINCT new com.ssafy.backend.domain.commercial.dto.info.CommercialInfo(a.districtCode, a.districtCodeName, a.administrationCode, a.administrationCodeName, a.commercialCode, a.commercialCodeName)"
            +
            "from AreaCommercial a " +
            "where a.commercialCodeName = :commercialCodeName")
    CommercialInfo findCommercialInfoByCommercialCodeName(String commercialCodeName);

}
