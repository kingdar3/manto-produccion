package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for emergency alert persistence.
 */
public interface EmergencyAlertRepositoryPort {

    EmergencyAlert save(EmergencyAlert emergencyAlert);

    Optional<EmergencyAlert> findById(EmergencyAlertId id);

    List<EmergencyAlert> findActiveByHostId(UserId hostId);

    List<EmergencyAlert> findActiveByProtectedUserId(UserId protectedUserId);

    List<EmergencyAlert> findByProtectedUserId(UserId protectedUserId);

    List<EmergencyAlert> findByHostUserId(UserId hostId);
}
