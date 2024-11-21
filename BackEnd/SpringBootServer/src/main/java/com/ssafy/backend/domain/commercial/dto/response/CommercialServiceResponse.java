package com.ssafy.backend.domain.commercial.dto.response;

import com.ssafy.backend.domain.district.entity.enums.ServiceType;
import lombok.Builder;

/**
 * 개별 서비스 업종의 코드와 이름, 서비스 타입(대분류)을 반환하는 응답 DTO입니다.
 */
@Builder
public record CommercialServiceResponse(
    String serviceCode,
    String serviceCodeName,
    ServiceType serviceType
) {

}
