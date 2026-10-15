package com.example.demo.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private String message;
    private int status;
    private String path;
    private String error;
    private Map<String, String> validationErrors;
}
