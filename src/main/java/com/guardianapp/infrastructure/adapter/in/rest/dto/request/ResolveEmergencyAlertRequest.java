package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import com.guardianapp.domain.enums.EmergencyResolutionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for resolving an emergency alert.
 */
public record ResolveEmergencyAlertRequest(
        @NotNull(message = "Host user ID is required")
        UUID hostId,

        @NotNull(message = "Resolution type is required")
        EmergencyResolutionType resolutionType,

        @Size(max = 500, message = "Note must not exceed 500 characters")
        String note
) {
}
