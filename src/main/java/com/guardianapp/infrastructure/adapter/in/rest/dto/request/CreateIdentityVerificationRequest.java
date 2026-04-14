package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO to create identity verification.
 */
public record CreateIdentityVerificationRequest(
    @NotNull(message = "Link ID is required")
    UUID linkId,

    @NotNull(message = "Protected user ID is required")
    UUID protectedUserId,

    @NotBlank(message = "Claimed person is required")
    @Size(max = 100, message = "Claimed person must not exceed 100 characters")
    String claimedPerson
) {}
