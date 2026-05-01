package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.EmergencyAlert;

/**
 * Output port for emergency notifications.
 */
public interface EmergencyNotificationPort {

    void notifyEmergencyTriggered(EmergencyAlert emergencyAlert);

    void notifyEmergencyResolved(EmergencyAlert emergencyAlert);
}
