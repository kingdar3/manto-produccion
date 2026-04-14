package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.VerificationStatus;
import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;
import com.guardianapp.domain.port.out.IdentityVerificationRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.IdentityVerificationEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.IdentityVerificationPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.IdentityVerificationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing identity verification repository port using JPA.
 */
@Component
public class IdentityVerificationRepositoryAdapter implements IdentityVerificationRepositoryPort {

    private final IdentityVerificationJpaRepository jpaRepository;
    private final IdentityVerificationPersistenceMapper mapper;

    public IdentityVerificationRepositoryAdapter(
            IdentityVerificationJpaRepository jpaRepository,
            IdentityVerificationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public IdentityVerification save(IdentityVerification verification) {
        IdentityVerificationEntity entity = mapper.toEntity(verification);
        IdentityVerificationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IdentityVerification> findById(VerificationId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<IdentityVerification> findPendingByHost(UserId hostId) {
        return jpaRepository
            .findByHostUserIdAndStatusOrderByCreatedAtDesc(hostId.getValue(), VerificationStatus.PENDING)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<IdentityVerification> findByProtected(UserId protectedUserId) {
        return jpaRepository
            .findByProtectedUserIdOrderByCreatedAtDesc(protectedUserId.getValue())
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
}
