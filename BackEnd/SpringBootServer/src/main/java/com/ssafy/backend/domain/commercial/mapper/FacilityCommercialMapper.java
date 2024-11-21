package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialSchoolInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFacilityResponse;
import com.ssafy.backend.domain.commercial.entity.FacilityCommercial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityCommercialMapper {

    // FacilityCommercial -> CommercialSchoolInfo 매핑
    default CommercialSchoolInfo entityToShcoolInfo(FacilityCommercial facilityCommercial) {

        // 학교 수 합산 처리
        long totalSchoolsCount = facilityCommercial.getElementarySchoolCnt()
            + facilityCommercial.getMiddleSchoolCnt()
            + facilityCommercial.getHighSchoolCnt();

        return CommercialSchoolInfo.builder()
            .totalSchoolsCount(totalSchoolsCount)
            .universityCount(facilityCommercial.getUniversityCnt())
            .build();
    }

    default CommercialFacilityResponse toCommercialFacilityResponse(
        FacilityCommercial facilityCommercial) {

        long facilityCnt = facilityCommercial.getFacilityCnt();
        long totalTransportCnt =
            facilityCommercial.getSubwayStationCnt() + facilityCommercial.getBusStopCnt();

        return CommercialFacilityResponse.builder()
            .facilityCnt(facilityCnt)
            .commercialSchoolInfo(entityToShcoolInfo(facilityCommercial))
            .totalTransportCnt(totalTransportCnt)
            .build();
    }
}
