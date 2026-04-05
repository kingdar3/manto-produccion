package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for link creation requests.
 */
public record CreateLinkRequest(
    @NotBlank(message = "Protected user ID is required")
    String protectedId
) {}
