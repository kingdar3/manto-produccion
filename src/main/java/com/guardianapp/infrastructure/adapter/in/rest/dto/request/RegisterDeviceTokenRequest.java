package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for registering a mobile push token.
 */
public record RegisterDeviceTokenRequest(
    @NotBlank(message = "Token is required")
    @Size(max = 512, message = "Token too long")
    String token,

    @Size(max = 32, message = "Platform too long")
    String platform
) {}
