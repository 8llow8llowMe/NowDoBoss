package com.ssafy.backend.domain.commercial.dto.response;

/**
 * 자치구에 따른 행정동 정보를 나타내는 DTO입니다. 이 record는 API 응답으로 사용되어, 요청받은 자치구에 속하는 행정동의 이름과 코드를 제공합니다.
 */
public record CommercialAdministrationAreaResponse(
    String administrationCodeName,
    String administrationCode
) {

}