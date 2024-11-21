package com.ssafy.backend.domain.commercial.dto.response;


import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGroupFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDayOfWeekFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSlotFootTrafficInfo;
import lombok.Builder;

@Builder
public record CommercialFootTrafficResponse(
    CommercialTimeSlotFootTrafficInfo timeSlotFootTraffic,
    CommercialDayOfWeekFootTrafficInfo dayOfWeekFootTraffic,
    CommercialAgeGroupFootTrafficInfo ageGroupFootTraffic,
    CommercialAgeGenderPercentFootTrafficInfo ageGenderPercentFootTraffic
) {

}
