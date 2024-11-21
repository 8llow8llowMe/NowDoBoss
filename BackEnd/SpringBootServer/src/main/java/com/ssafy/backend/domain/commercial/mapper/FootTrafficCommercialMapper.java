package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGroupFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDayOfWeekFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSlotFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFootTrafficResponse;
import com.ssafy.backend.domain.commercial.entity.FootTrafficCommercial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FootTrafficCommercialMapper {

    // FootTrafficCommercial -> 각 DTO 매핑
    CommercialTimeSlotFootTrafficInfo entityToTimeSlotFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    CommercialDayOfWeekFootTrafficInfo entityToDayOfWeekFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    CommercialAgeGroupFootTrafficInfo entityToAgeGroupFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial);

    default CommercialFootTrafficResponse toCommercialFootTrafficResponse(
        FootTrafficCommercial footTrafficCommercial,
        CommercialAgeGenderPercentFootTrafficInfo ageGenderPercentFootTrafficInfo) {

        return CommercialFootTrafficResponse.builder()
            .timeSlotFootTraffic(entityToTimeSlotFootTrafficInfo(footTrafficCommercial))
            .dayOfWeekFootTraffic(entityToDayOfWeekFootTrafficInfo(footTrafficCommercial))
            .ageGroupFootTraffic(entityToAgeGroupFootTrafficInfo(footTrafficCommercial))
            .ageGenderPercentFootTraffic(ageGenderPercentFootTrafficInfo)
            .build();
    }
}
