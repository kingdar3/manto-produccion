package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for link confirmation requests.
 */
public record ConfirmLinkRequest(
    @NotBlank(message = "Connection code is required")
    @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits")
    String connectionCode
) {}
