package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.port.out.EmergencyAudioRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.EmergencyAudioRecordingPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.EmergencyAudioRecordingJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing emergency audio repository port.
 */
@Component
public class EmergencyAudioRepositoryAdapter implements EmergencyAudioRepositoryPort {

    private final EmergencyAudioRecordingJpaRepository jpaRepository;
    private final EmergencyAudioRecordingPersistenceMapper mapper;

    public EmergencyAudioRepositoryAdapter(
            EmergencyAudioRecordingJpaRepository jpaRepository,
            EmergencyAudioRecordingPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public EmergencyAudioRecording save(EmergencyAudioRecording recording) {
        var entity = mapper.toEntity(recording);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EmergencyAudioRecording> findLatestByEmergencyAlertId(EmergencyAlertId emergencyAlertId) {
        return jpaRepository.findTopByEmergencyAlertIdOrderByCreatedAtDesc(emergencyAlertId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<EmergencyAudioRecording> findByEmergencyAlertId(EmergencyAlertId emergencyAlertId) {
        return jpaRepository.findByEmergencyAlertIdOrderByCreatedAtDesc(emergencyAlertId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
