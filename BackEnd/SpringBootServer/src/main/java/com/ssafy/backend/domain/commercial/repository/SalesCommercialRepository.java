package com.ssafy.backend.domain.commercial.repository;

import com.ssafy.backend.domain.commercial.entity.SalesCommercial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesCommercialRepository extends JpaRepository<SalesCommercial, Long>,
    SalesCommercialCustom {

    @Query(value = "SELECT DISTINCT sc.service_code as serviceCode, " +
        "sc.service_code_name as serviceCodeName, " +
        "sc.service_type as serviceType " +
        "FROM sales_commercial sc " +
        "WHERE sc.commercial_code = :commercialCode " +
        "ORDER BY sc.service_code", nativeQuery = true)
    List<ServiceCodeProjection> findDistinctServiceCodesByCommercialCode(String commercialCode);

    Optional<SalesCommercial> findByPeriodCodeAndCommercialCodeAndServiceCode(String periodCode,
        String commercialCode, String serviceCode);

    List<SalesCommercial> findByPeriodCodeInAndCommercialCodeAndServiceCode(
        List<String> periodCodes, String commercialCode, String serviceCode);
}

