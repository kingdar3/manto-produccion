package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for triggering an emergency alert.
 */
public record TriggerEmergencyAlertRequest(
        @NotNull(message = "Link ID is required")
        UUID linkId,

        @NotNull(message = "Protected user ID is required")
        UUID protectedUserId,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double longitude
) {
}
