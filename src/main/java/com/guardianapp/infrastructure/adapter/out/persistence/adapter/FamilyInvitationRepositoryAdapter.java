package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.FamilyInvitationStatus;
import com.guardianapp.domain.model.FamilyInvitation;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.FamilyInvitationId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.FamilyInvitationRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyInvitationEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.FamilyInvitationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing family invitation repository port using JPA.
 */
@Component
public class FamilyInvitationRepositoryAdapter implements FamilyInvitationRepositoryPort {

    private final FamilyInvitationJpaRepository jpaRepository;

    public FamilyInvitationRepositoryAdapter(FamilyInvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FamilyInvitation save(FamilyInvitation invitation) {
        FamilyInvitationEntity entity = toEntity(invitation);
        FamilyInvitationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FamilyInvitation> findById(FamilyInvitationId invitationId) {
        return jpaRepository.findById(invitationId.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<FamilyInvitation> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public List<FamilyInvitation> findByFamilyGroupId(FamilyGroupId familyGroupId) {
        return jpaRepository.findByFamilyGroupIdOrderByCreatedAtDesc(familyGroupId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<FamilyInvitation> findPendingByInviter(UserId inviterUserId) {
        return jpaRepository.findByInvitedByUserIdAndStatusOrderByCreatedAtDesc(
                        inviterUserId.getValue(),
                        FamilyInvitationStatus.PENDING)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private FamilyInvitationEntity toEntity(FamilyInvitation invitation) {
        return FamilyInvitationEntity.builder()
                .id(invitation.getId().getValue())
                .familyGroupId(invitation.getFamilyGroupId().getValue())
                .invitedByUserId(invitation.getInvitedByUserId().getValue())
                .targetRole(invitation.getTargetRole())
                .token(invitation.getToken())
                .status(invitation.getStatus())
                .expiresAt(invitation.getExpiresAt())
                .createdAt(invitation.getCreatedAt())
                .acceptedAt(invitation.getAcceptedAt())
                .acceptedByUserId(invitation.getAcceptedByUserId() != null ? invitation.getAcceptedByUserId().getValue() : null)
                .build();
    }

    private FamilyInvitation toDomain(FamilyInvitationEntity entity) {
        return FamilyInvitation.reconstitute(
                FamilyInvitationId.of(entity.getId()),
                FamilyGroupId.of(entity.getFamilyGroupId()),
                UserId.of(entity.getInvitedByUserId()),
                entity.getTargetRole(),
                entity.getToken(),
                entity.getStatus(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getAcceptedAt(),
                entity.getAcceptedByUserId() != null ? UserId.of(entity.getAcceptedByUserId()) : null
        );
    }
}
