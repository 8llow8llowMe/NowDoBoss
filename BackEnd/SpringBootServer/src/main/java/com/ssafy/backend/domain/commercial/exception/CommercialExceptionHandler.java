package com.ssafy.backend.domain.commercial.exception;

import com.ssafy.backend.global.common.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class CommercialExceptionHandler {

    @ExceptionHandler(CoordinateTransformationException.class)
    public ResponseEntity<Response<String>> handleCoordinateTransformationException(
        CoordinateTransformationException ex) {
        log.error("좌표 변환 오류: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Response.fail("coordinateTransformationError", "좌표 변환 중 오류가 발생했습니다."));
    }

    @ExceptionHandler(CommercialException.class)
    public ResponseEntity<Response<Void>> commercialException(CommercialException e) {
        log.error("상권 관련 오류: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(Response.fail(null, e.getMessage()));
    }
}
