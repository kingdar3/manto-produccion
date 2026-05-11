package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.Optional;

/**
 * Input port for emergency audio operations.
 */
public interface EmergencyAudioUseCase {

    EmergencyAudioRecording uploadAudio(UploadEmergencyAudioCommand command);

    Optional<EmergencyAudioRecording> getLatestByEmergency(EmergencyAlertId emergencyAlertId);

    record UploadEmergencyAudioCommand(
            EmergencyAlertId emergencyAlertId,
            UserId protectedUserId,
            byte[] audioBytes,
            String contentType,
            Integer durationSeconds
    ) {
        public UploadEmergencyAudioCommand {
            if (emergencyAlertId == null) {
                throw new IllegalArgumentException("Emergency alert ID is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
            if (audioBytes == null || audioBytes.length == 0) {
                throw new IllegalArgumentException("Audio bytes are required");
            }
        }
    }
}
