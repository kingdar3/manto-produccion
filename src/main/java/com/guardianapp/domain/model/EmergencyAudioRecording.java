package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.EmergencyAudioStatus;
import com.guardianapp.domain.enums.EmergencyAudioStorageProvider;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.EmergencyAudioRecordingId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for emergency audio recordings.
 */
public class EmergencyAudioRecording {

    private final EmergencyAudioRecordingId id;
    private final EmergencyAlertId emergencyAlertId;
    private EmergencyAudioStorageProvider storageProvider;
    private EmergencyAudioStatus status;
    private String storageFileId;
    private String playbackUrl;
    private Integer durationSeconds;
    private Long fileSizeBytes;
    private final LocalDateTime createdAt;
    private LocalDateTime uploadedAt;

    private EmergencyAudioRecording(
            EmergencyAudioRecordingId id,
            EmergencyAlertId emergencyAlertId,
            EmergencyAudioStorageProvider storageProvider,
            EmergencyAudioStatus status,
            String storageFileId,
            String playbackUrl,
            Integer durationSeconds,
            Long fileSizeBytes,
            LocalDateTime createdAt,
            LocalDateTime uploadedAt) {
        this.id = Objects.requireNonNull(id, "Audio recording ID is required");
        this.emergencyAlertId = Objects.requireNonNull(emergencyAlertId, "Emergency alert ID is required");
        this.storageProvider = Objects.requireNonNull(storageProvider, "Storage provider is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.storageFileId = storageFileId;
        this.playbackUrl = playbackUrl;
        this.durationSeconds = durationSeconds;
        this.fileSizeBytes = fileSizeBytes;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at is required");
        this.uploadedAt = uploadedAt;
    }

    public static EmergencyAudioRecording start(EmergencyAlertId emergencyAlertId) {
        return new EmergencyAudioRecording(
                EmergencyAudioRecordingId.generate(),
                emergencyAlertId,
                EmergencyAudioStorageProvider.GOOGLE_DRIVE,
                EmergencyAudioStatus.RECORDING,
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public static EmergencyAudioRecording reconstitute(
            EmergencyAudioRecordingId id,
            EmergencyAlertId emergencyAlertId,
            EmergencyAudioStorageProvider storageProvider,
            EmergencyAudioStatus status,
            String storageFileId,
            String playbackUrl,
            Integer durationSeconds,
            Long fileSizeBytes,
            LocalDateTime createdAt,
            LocalDateTime uploadedAt) {
        return new EmergencyAudioRecording(
                id,
                emergencyAlertId,
                storageProvider,
                status,
                storageFileId,
                playbackUrl,
                durationSeconds,
                fileSizeBytes,
                createdAt,
                uploadedAt
        );
    }

    public void markUploaded(
            EmergencyAudioStorageProvider storageProvider,
            String storageFileId,
            String playbackUrl,
            Integer durationSeconds,
            Long fileSizeBytes) {
        this.storageProvider = Objects.requireNonNull(storageProvider, "Storage provider is required");
        this.storageFileId = storageFileId;
        this.playbackUrl = playbackUrl;
        this.durationSeconds = durationSeconds;
        this.fileSizeBytes = fileSizeBytes;
        this.status = EmergencyAudioStatus.UPLOADED;
        this.uploadedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = EmergencyAudioStatus.FAILED;
    }

    public EmergencyAudioRecordingId getId() {
        return id;
    }

    public EmergencyAlertId getEmergencyAlertId() {
        return emergencyAlertId;
    }

    public EmergencyAudioStorageProvider getStorageProvider() {
        return storageProvider;
    }

    public EmergencyAudioStatus getStatus() {
        return status;
    }

    public String getStorageFileId() {
        return storageFileId;
    }

    public String getPlaybackUrl() {
        return playbackUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
