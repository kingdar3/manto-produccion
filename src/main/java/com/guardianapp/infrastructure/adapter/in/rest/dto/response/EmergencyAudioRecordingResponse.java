package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.EmergencyAudioStatus;
import com.guardianapp.domain.enums.EmergencyAudioStorageProvider;
import com.guardianapp.domain.model.EmergencyAudioRecording;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for emergency audio recording.
 */
public record EmergencyAudioRecordingResponse(
        UUID id,
        UUID emergencyAlertId,
        EmergencyAudioStorageProvider storageProvider,
        EmergencyAudioStatus status,
        String storageFileId,
        String playbackUrl,
        Integer durationSeconds,
        Long fileSizeBytes,
        LocalDateTime createdAt,
        LocalDateTime uploadedAt
) {
    public static EmergencyAudioRecordingResponse from(EmergencyAudioRecording recording) {
        return new EmergencyAudioRecordingResponse(
                recording.getId().getValue(),
                recording.getEmergencyAlertId().getValue(),
                recording.getStorageProvider(),
                recording.getStatus(),
                recording.getStorageFileId(),
                recording.getPlaybackUrl(),
                recording.getDurationSeconds(),
                recording.getFileSizeBytes(),
                recording.getCreatedAt(),
                recording.getUploadedAt()
        );
    }
}
