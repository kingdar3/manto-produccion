package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;

/**
 * Input port for emergency audio history retrieval.
 */
public interface EmergencyAudioHistoryUseCase {

    List<EmergencyAudioRecording> getByEmergencyId(EmergencyAlertId emergencyAlertId, UserId requesterId);
}
