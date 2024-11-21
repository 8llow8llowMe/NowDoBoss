package com.ssafy.backend.domain.commercial.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommercialInfo extends AdministrationInfo {

    private String commercialCode;
    private String commercialCodeName;

    public CommercialInfo(String districtCode, String districtCodeName, String administrationCode,
        String administrationCodeName, String commercialCode, String commercialCodeName) {
        super(districtCode, districtCodeName, administrationCode, administrationCodeName);
        this.commercialCode = commercialCode;
        this.commercialCodeName = commercialCodeName;
    }
}
