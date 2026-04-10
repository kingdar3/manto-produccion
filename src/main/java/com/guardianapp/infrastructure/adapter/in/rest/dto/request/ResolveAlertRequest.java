package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for resolving an alert.
 */
public record ResolveAlertRequest(
    @NotNull(message = "Host user ID is required")
    UUID hostId,

    @NotNull(message = "Allow access decision is required")
    Boolean allowAccess,

    @Size(max = 500, message = "Note must not exceed 500 characters")
    String note
) {}
