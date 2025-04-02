package com.ssafy.backend.domain.district.exception;

import com.ssafy.backend.global.common.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DistrictExceptionHandler {

    @ExceptionHandler(DistrictException.class)
    public ResponseEntity<Response<Void>> districtException(DistrictException e) {
        log.error("자치구 관련 오류: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(Response.fail(null, e.getMessage()));
    }
}
