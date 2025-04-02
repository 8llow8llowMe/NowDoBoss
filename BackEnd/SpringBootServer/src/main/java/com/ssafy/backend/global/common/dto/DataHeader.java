package com.ssafy.backend.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataHeader {

    private final boolean success;
    private final String resultCode;
    private final Object resultMessage;

    public static DataHeader success() {
        return DataHeader.builder()
            .success(true)
            .build();
    }

    public static DataHeader fail(String resultCode, Object resultMessage) {
        return DataHeader.builder()
            .success(false)
            .resultCode(resultCode)
            .resultMessage(resultMessage)
            .build();
    }
}
