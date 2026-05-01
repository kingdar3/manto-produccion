package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.EmergencyAlertStatus;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.EmergencyAlertPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.EmergencyAlertJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing emergency alert repository port using JPA.
 */
@Component
public class EmergencyAlertRepositoryAdapter implements EmergencyAlertRepositoryPort {

    private final EmergencyAlertJpaRepository jpaRepository;
    private final EmergencyAlertPersistenceMapper mapper;

    public EmergencyAlertRepositoryAdapter(
            EmergencyAlertJpaRepository jpaRepository,
            EmergencyAlertPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public EmergencyAlert save(EmergencyAlert emergencyAlert) {
        var entity = mapper.toEntity(emergencyAlert);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EmergencyAlert> findById(EmergencyAlertId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<EmergencyAlert> findActiveByHostId(UserId hostId) {
        return jpaRepository.findByPrimaryHostUserIdAndStatusOrderByCreatedAtDesc(
                        hostId.getValue(),
                        EmergencyAlertStatus.ACTIVE)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<EmergencyAlert> findActiveByProtectedUserId(UserId protectedUserId) {
        return jpaRepository.findByProtectedUserIdAndStatusOrderByCreatedAtDesc(
                        protectedUserId.getValue(),
                        EmergencyAlertStatus.ACTIVE)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<EmergencyAlert> findByProtectedUserId(UserId protectedUserId) {
        return jpaRepository.findByProtectedUserIdOrderByCreatedAtDesc(protectedUserId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
