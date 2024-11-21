package com.ssafy.backend.domain.commercial.dto.info;

import lombok.Builder;

/**
 * 해당 상권 내의 학교 수 정보를 담는 DTO.
 */
@Builder
public record CommercialSchoolInfo(
    long totalSchoolsCount, // 총 학교 수 (초등 + 중등 + 고등)
    long universityCount // 대학교 수
) {

}
