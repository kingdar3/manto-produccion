package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating an alert.
 */
public record CreateAlertRequest(
    @NotNull(message = "Link ID is required")
    UUID linkId,

    @NotNull(message = "Protected user ID is required")
    UUID protectedUserId,

    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    String url,

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    String reason
) {}
