package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for emergency audio recording persistence.
 */
public interface EmergencyAudioRepositoryPort {

    EmergencyAudioRecording save(EmergencyAudioRecording recording);

    Optional<EmergencyAudioRecording> findLatestByEmergencyAlertId(EmergencyAlertId emergencyAlertId);

    List<EmergencyAudioRecording> findByEmergencyAlertId(EmergencyAlertId emergencyAlertId);
}
