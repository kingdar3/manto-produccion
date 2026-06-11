package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO to resolve an SMS threat alert.
 */
public record ResolveSmsThreatAlertRequest(
    @NotNull(message = "Host ID is required")
    UUID hostId,

    @NotNull(message = "allowAccess is required")
    Boolean allowAccess,

    @Size(max = 500, message = "Note must not exceed 500 characters")
    String note
) {}
