package com.guardianapp.domain.exception;

/**
 * Domain exception for emergency audio operations.
 */
public class EmergencyAudioException extends DomainException {

    private EmergencyAudioException(String message, String code) {
        super(message, code);
    }

    public static EmergencyAudioException emergencyNotFound(String emergencyId) {
        return new EmergencyAudioException(
                "Emergency alert not found with ID: " + emergencyId,
                "EMERGENCY_AUDIO_ALERT_NOT_FOUND"
        );
    }

    public static EmergencyAudioException emergencyNotActive(String emergencyId) {
        return new EmergencyAudioException(
                "Emergency alert is not active: " + emergencyId,
                "EMERGENCY_AUDIO_ALERT_NOT_ACTIVE"
        );
    }

    public static EmergencyAudioException notAuthorized(String userId) {
        return new EmergencyAudioException(
                "User " + userId + " is not authorized for this emergency audio operation",
                "EMERGENCY_AUDIO_NOT_AUTHORIZED"
        );
    }

    public static EmergencyAudioException uploadFailed(String reason) {
        return new EmergencyAudioException(
                "Emergency audio upload failed: " + reason,
                "EMERGENCY_AUDIO_UPLOAD_FAILED"
        );
    }
}
