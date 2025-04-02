package com.ssafy.backend.global.common.dto;

public record Response<T>(DataHeader dataHeader, T dataBody) {

    public static <T> Response<T> success(T dataBody) {
        return new Response<>(DataHeader.success(), dataBody);
    }

    public static Response<Void> success() {
        return new Response<>(DataHeader.success(), null);
    }

    public static <T> Response<T> fail(String resultCode, Object resultMessage) {
        return new Response<>(DataHeader.fail(resultCode, resultMessage), null);
    }
}
