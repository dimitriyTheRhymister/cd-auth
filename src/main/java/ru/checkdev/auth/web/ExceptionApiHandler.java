package ru.checkdev.auth.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.checkdev.auth.util.CircuitBreaker;

import java.util.Map;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(CircuitBreaker.CircuitBreakerOpenException.class)
    public ResponseEntity<Map<String, String>> handleCircuitBreakerOpen(CircuitBreaker.CircuitBreakerOpenException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", e.getMessage()));
    }
}