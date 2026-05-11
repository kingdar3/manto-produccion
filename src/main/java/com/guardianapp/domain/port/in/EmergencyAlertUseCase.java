package com.guardianapp.domain.port.in;

import com.guardianapp.domain.enums.EmergencyResolutionType;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for emergency alert operations.
 */
public interface EmergencyAlertUseCase {

    EmergencyAlert trigger(TriggerEmergencyCommand command);

    EmergencyAlert resolve(ResolveEmergencyCommand command);

    Optional<EmergencyAlert> getById(EmergencyAlertId alertId);

    List<EmergencyAlert> getActiveForHost(UserId hostId);

    List<EmergencyAlert> getActiveForProtected(UserId protectedUserId);

    List<EmergencyAlert> getByProtectedUser(UserId protectedUserId);

    record TriggerEmergencyCommand(
            LinkId linkId,
            UserId protectedUserId,
            double latitude,
            double longitude
    ) {
        public TriggerEmergencyCommand {
            if (linkId == null) {
                throw new IllegalArgumentException("Link ID is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
        }
    }

    record ResolveEmergencyCommand(
            EmergencyAlertId emergencyAlertId,
            UserId hostUserId,
            EmergencyResolutionType resolutionType,
            String note
    ) {
        public ResolveEmergencyCommand {
            if (emergencyAlertId == null) {
                throw new IllegalArgumentException("Emergency alert ID is required");
            }
            if (hostUserId == null) {
                throw new IllegalArgumentException("Host user ID is required");
            }
            if (resolutionType == null) {
                throw new IllegalArgumentException("Resolution type is required");
            }
        }
    }
}
