package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.administration.dto.info.AdministrationTotalIncomeInfo;
import com.ssafy.backend.domain.administration.entity.IncomeAdministration;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAvgIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTotalIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTypeIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.response.AllIncomeResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialIncomeResponse;
import com.ssafy.backend.domain.commercial.entity.IncomeCommercial;
import com.ssafy.backend.domain.district.dto.info.DistrictTotalIncomeInfo;
import com.ssafy.backend.domain.district.entity.IncomeDistrict;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncomeCommercialMapper {

    DistrictTotalIncomeInfo entityToDistrictTotalIncomeInfo(IncomeDistrict incomeDistrict);

    AdministrationTotalIncomeInfo entityToAdministrationTotalIncomeInfo(
        IncomeAdministration incomeAdministration);

    CommercialTotalIncomeInfo entityToCommercialTotalIncomeInfo(IncomeCommercial incomeCommercial);

    CommercialAvgIncomeInfo entityToCommercialAvgIncomeInfo(IncomeCommercial incomeCommercial);

    CommercialAnnualQuarterIncomeInfo entityToCommercialAnnualQuarterIncomeInfo(
        IncomeCommercial incomeCommercial);

    CommercialTypeIncomeInfo entityToCommercialTypeIncomeInfo(IncomeCommercial incomeCommercial);

    default AllIncomeResponse toAllIncomeResponse(
        IncomeDistrict incomeDistrict,
        IncomeAdministration incomeAdministration,
        IncomeCommercial incomeCommercial) {

        return AllIncomeResponse.builder()
            .districtTotalIncomeInfo(entityToDistrictTotalIncomeInfo(incomeDistrict))
            .administrationTotalIncomeInfo(
                entityToAdministrationTotalIncomeInfo(incomeAdministration))
            .commercialTotalIncomeInfo(entityToCommercialTotalIncomeInfo(incomeCommercial))
            .build();
    }

    default CommercialIncomeResponse toCommercialIncomeResponse(
        IncomeCommercial incomeCommercial,
        List<CommercialAnnualQuarterIncomeInfo> annualQuarterIncomeInfos) {

        return CommercialIncomeResponse.builder()
            .avgIncomeInfo(entityToCommercialAvgIncomeInfo(incomeCommercial))
            .annualQuarterIncomeInfos(annualQuarterIncomeInfos)
            .typeIncomeInfo(entityToCommercialTypeIncomeInfo(incomeCommercial))
            .build();
    }
}
