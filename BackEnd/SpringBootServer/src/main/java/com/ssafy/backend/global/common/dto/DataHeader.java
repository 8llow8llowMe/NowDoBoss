package com.ssafy.backend.global.common.dto;

public record DataHeader(int successCode, String resultCode, Object resultMessage) {

    public static DataHeader success() {
        return new DataHeader(0, null, null);
    }

    public static DataHeader fail(String resultCode, Object resultMessage) {
        return new DataHeader(1, resultCode, resultMessage);
    }
}
