package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.VerificationStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.IdentityVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for identity verifications.
 */
@Repository
public interface IdentityVerificationJpaRepository extends JpaRepository<IdentityVerificationEntity, UUID> {

    List<IdentityVerificationEntity> findByHostUserIdAndStatusOrderByCreatedAtDesc(UUID hostUserId, VerificationStatus status);

    List<IdentityVerificationEntity> findByProtectedUserIdOrderByCreatedAtDesc(UUID protectedUserId);
}
