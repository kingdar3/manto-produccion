package com.guardianapp.application.service;

import com.guardianapp.domain.enums.EmergencyAudioStorageProvider;
import com.guardianapp.domain.exception.EmergencyAudioException;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.port.in.EmergencyAudioUseCase;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAudioRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAudioStoragePort;

import java.util.Locale;
import java.util.Optional;

/**
 * Application service implementing emergency audio use cases.
 */
public class EmergencyAudioService implements EmergencyAudioUseCase {

    private final EmergencyAlertRepositoryPort emergencyAlertRepository;
    private final EmergencyAudioRepositoryPort emergencyAudioRepository;
    private final EmergencyAudioStoragePort emergencyAudioStorage;

    public EmergencyAudioService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            EmergencyAudioRepositoryPort emergencyAudioRepository,
            EmergencyAudioStoragePort emergencyAudioStorage) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.emergencyAudioRepository = emergencyAudioRepository;
        this.emergencyAudioStorage = emergencyAudioStorage;
    }

    @Override
    public EmergencyAudioRecording uploadAudio(UploadEmergencyAudioCommand command) {
        EmergencyAlert emergencyAlert = emergencyAlertRepository.findById(command.emergencyAlertId())
                .orElseThrow(() -> EmergencyAudioException.emergencyNotFound(command.emergencyAlertId().toString()));

        if (!emergencyAlert.getProtectedUserId().equals(command.protectedUserId())) {
            throw EmergencyAudioException.notAuthorized(command.protectedUserId().toString());
        }

        if (!emergencyAlert.isActive() && !emergencyAlert.isResolved()) {
            throw EmergencyAudioException.emergencyNotActive(command.emergencyAlertId().toString());
        }

        EmergencyAudioRecording recording = EmergencyAudioRecording.start(command.emergencyAlertId());
        recording = emergencyAudioRepository.save(recording);

        String fileName = String.format(
                Locale.ROOT,
                "emergency-%s-%s.m4a",
                emergencyAlert.getId().getValue(),
                System.currentTimeMillis());

        try {
            EmergencyAudioStoragePort.UploadResult uploadResult = emergencyAudioStorage.upload(
                    fileName,
                    command.audioBytes(),
                    command.contentType() != null ? command.contentType() : "audio/m4a");

            EmergencyAudioStorageProvider provider = parseProvider(uploadResult.provider());
            recording.markUploaded(
                    provider,
                    uploadResult.fileId(),
                    uploadResult.playbackUrl(),
                    command.durationSeconds(),
                    (long) command.audioBytes().length);
            return emergencyAudioRepository.save(recording);
        } catch (Exception ex) {
            recording.markFailed();
            emergencyAudioRepository.save(recording);
            throw EmergencyAudioException.uploadFailed(ex.getMessage());
        }
    }

    @Override
    public Optional<EmergencyAudioRecording> getLatestByEmergency(EmergencyAlertId emergencyAlertId) {
        return emergencyAudioRepository.findLatestByEmergencyAlertId(emergencyAlertId);
    }

    private EmergencyAudioStorageProvider parseProvider(String provider) {
        if (provider == null) {
            return EmergencyAudioStorageProvider.LOCAL;
        }
        if ("GOOGLE_DRIVE".equalsIgnoreCase(provider)) {
            return EmergencyAudioStorageProvider.GOOGLE_DRIVE;
        }
        return EmergencyAudioStorageProvider.LOCAL;
    }
}
