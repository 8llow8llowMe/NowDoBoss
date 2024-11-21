package com.ssafy.backend.domain.commercial.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdministrationInfo extends DistrictInfo {

    private String administrationCode;
    private String administrationCodeName;

    public AdministrationInfo(String districtCode, String districtCodeName,
        String administrationCode, String administrationCodeName) {
        super(districtCode, districtCodeName);
        this.administrationCode = administrationCode;
        this.administrationCodeName = administrationCodeName;
    }
}

