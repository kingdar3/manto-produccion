package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.EmergencyAlertStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.EmergencyAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for emergency alerts.
 */
@Repository
public interface EmergencyAlertJpaRepository extends JpaRepository<EmergencyAlertEntity, UUID> {

    List<EmergencyAlertEntity> findByPrimaryHostUserIdAndStatusOrderByCreatedAtDesc(
            UUID hostUserId,
            EmergencyAlertStatus status);

    List<EmergencyAlertEntity> findByProtectedUserIdAndStatusOrderByCreatedAtDesc(
            UUID protectedUserId,
            EmergencyAlertStatus status);

    List<EmergencyAlertEntity> findByProtectedUserIdOrderByCreatedAtDesc(UUID protectedUserId);

    List<EmergencyAlertEntity> findByPrimaryHostUserIdOrderByCreatedAtDesc(UUID hostUserId);
}
