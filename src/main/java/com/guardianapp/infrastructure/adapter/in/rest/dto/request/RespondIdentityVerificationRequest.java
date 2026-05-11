package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO to respond an identity verification.
 */
public record RespondIdentityVerificationRequest(
    @NotNull(message = "Host user ID is required")
    UUID hostUserId,

    @NotNull(message = "Approved flag is required")
    Boolean approved,

    @Size(max = 500, message = "Note must not exceed 500 characters")
    String note
) {}
