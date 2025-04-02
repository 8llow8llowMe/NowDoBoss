package com.ssafy.backend.domain.simulation.exception;

import com.ssafy.backend.global.common.dto.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SimulationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidInputExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        // 오류 메시지를 순서대로 추가합니다.
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName + "Error", message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Response.fail("validError", errors));
    }

    @ExceptionHandler(SimulationException.class)
    public ResponseEntity<Response<Void>> memberException(SimulationException e) {
        log.error("창업 시뮬레이션 관련 오류: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
            Response.fail(null, e.getErrorCode().getErrorMessage()));
    }
}
