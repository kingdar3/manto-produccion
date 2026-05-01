package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.EmergencyAlertStatus;
import com.guardianapp.domain.enums.EmergencyResolutionType;
import com.guardianapp.domain.model.EmergencyAlert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for emergency alerts.
 */
public record EmergencyAlertResponse(
        UUID id,
        UUID linkId,
        UUID protectedUserId,
        UUID primaryHostUserId,
        double latitude,
        double longitude,
        EmergencyAlertStatus status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        UUID resolvedByUserId,
        EmergencyResolutionType resolutionType,
        String resolutionNote,
        long secondsActive
) {
    public static EmergencyAlertResponse from(EmergencyAlert emergencyAlert) {
        return new EmergencyAlertResponse(
                emergencyAlert.getId().getValue(),
                emergencyAlert.getLinkId().getValue(),
                emergencyAlert.getProtectedUserId().getValue(),
                emergencyAlert.getPrimaryHostUserId().getValue(),
                emergencyAlert.getLatitude(),
                emergencyAlert.getLongitude(),
                emergencyAlert.getStatus(),
                emergencyAlert.getCreatedAt(),
                emergencyAlert.getResolvedAt(),
                emergencyAlert.getResolvedByUserId() != null
                        ? emergencyAlert.getResolvedByUserId().getValue()
                        : null,
                emergencyAlert.getResolutionType(),
                emergencyAlert.getResolutionNote(),
                emergencyAlert.isActive() ? emergencyAlert.getSecondsSinceCreation() : 0
        );
    }
}
