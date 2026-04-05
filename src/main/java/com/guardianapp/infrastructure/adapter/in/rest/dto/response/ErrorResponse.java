package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for API errors.
 */
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp,
    List<FieldError> errors
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now(), List.of());
    }

    public ErrorResponse(String code, String message, List<FieldError> errors) {
        this(code, message, LocalDateTime.now(), errors);
    }

    public record FieldError(
        String field,
        String message
    ) {}
}
