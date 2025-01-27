package com.example.springboot.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //처리되지 않은 모든 예외를 처리
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", "An unexpected error occurred",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "status", ex
                                .getStatus()
                                .value(),
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage()
                ));
    }
}
