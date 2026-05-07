package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.EmergencyAudioRecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for emergency audio recordings.
 */
@Repository
public interface EmergencyAudioRecordingJpaRepository extends JpaRepository<EmergencyAudioRecordingEntity, UUID> {

    Optional<EmergencyAudioRecordingEntity> findTopByEmergencyAlertIdOrderByCreatedAtDesc(UUID emergencyAlertId);

    List<EmergencyAudioRecordingEntity> findByEmergencyAlertIdOrderByCreatedAtDesc(UUID emergencyAlertId);
}
