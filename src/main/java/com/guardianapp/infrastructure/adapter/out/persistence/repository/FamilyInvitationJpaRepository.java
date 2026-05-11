package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.FamilyInvitationStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for family invitations.
 */
@Repository
public interface FamilyInvitationJpaRepository extends JpaRepository<FamilyInvitationEntity, UUID> {

    Optional<FamilyInvitationEntity> findByToken(String token);

    List<FamilyInvitationEntity> findByFamilyGroupIdOrderByCreatedAtDesc(UUID familyGroupId);

    List<FamilyInvitationEntity> findByInvitedByUserIdAndStatusOrderByCreatedAtDesc(UUID invitedByUserId, FamilyInvitationStatus status);
}
