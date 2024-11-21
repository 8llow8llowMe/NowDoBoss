package com.ssafy.backend.domain.commercial.mapper;

import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGroupFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialDayOfWeekFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialTimeSlotFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFootTrafficResponse;
import com.ssafy.backend.domain.commercial.entity.FootTrafficCommercial;
import com.ssafy.backend.global.util.PercentCalculator;
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

    default CommercialAgeGenderPercentFootTrafficInfo toCommercialAgeGenderPercentFootTrafficInfo(
        FootTrafficCommercial footTrafficCommercial) {

        long total = footTrafficCommercial.getTotalFootTraffic();

        return CommercialAgeGenderPercentFootTrafficInfo.builder()
            .maleTeenFootTrafficPercent( // 10대 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getTeenFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleTeenFootTrafficPercent( // 10대 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getTeenFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .maleTwentyFootTrafficPercent( // 20대 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getTwentyFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleTwentyFootTrafficPercent( // 20대 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getTwentyFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .maleThirtyFootTrafficPercent( // 30대 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getThirtyFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleThirtyFootTrafficPercent( // 30대 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getThirtyFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .maleFortyFootTrafficPercent( // 40대 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getFortyFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleFortyFootTrafficPercent( // 40대 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getFortyFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .maleFiftyFootTrafficPercent( // 50대 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getFiftyFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleFiftyFootTrafficPercent( // 50대 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getFiftyFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .femaleSixtyFootTrafficPercent( // 60대 이상 남자
                PercentCalculator.calculatePercent(footTrafficCommercial.getSixtyFootTraffic(),
                    footTrafficCommercial.getMaleFootTraffic(), total)
            )
            .femaleSixtyFootTrafficPercent( // 60대 이상 여자
                PercentCalculator.calculatePercent(footTrafficCommercial.getSixtyFootTraffic(),
                    footTrafficCommercial.getFemaleFootTraffic(), total)
            )
            .build();
    }
}
