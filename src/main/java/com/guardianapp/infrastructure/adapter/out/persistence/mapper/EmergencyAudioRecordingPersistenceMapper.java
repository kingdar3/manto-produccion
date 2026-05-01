package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.EmergencyAudioRecordingId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.EmergencyAudioRecordingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for emergency audio recording conversions.
 */
@Mapper(componentModel = "spring")
public interface EmergencyAudioRecordingPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "emergencyAlertId", source = "emergencyAlertId.value")
    @Mapping(target = "emergencyAlert", ignore = true)
    EmergencyAudioRecordingEntity toEntity(EmergencyAudioRecording recording);

    default EmergencyAudioRecording toDomain(EmergencyAudioRecordingEntity entity) {
        if (entity == null) {
            return null;
        }
        return EmergencyAudioRecording.reconstitute(
                EmergencyAudioRecordingId.of(entity.getId()),
                EmergencyAlertId.of(entity.getEmergencyAlertId()),
                entity.getStorageProvider(),
                entity.getStatus(),
                entity.getStorageFileId(),
                entity.getPlaybackUrl(),
                entity.getDurationSeconds(),
                entity.getFileSizeBytes(),
                entity.getCreatedAt(),
                entity.getUploadedAt()
        );
    }
}
