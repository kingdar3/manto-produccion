package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for invitations.
 */
@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, UUID> {

    /**
     * Finds an invitation by its token.
     */
    Optional<InvitationEntity> findByToken(String token);

    /**
     * Finds all invitations created by a host.
     */
    List<InvitationEntity> findByHostIdOrderByCreatedAtDesc(UUID hostId);

    /**
     * Finds invitations by host and status.
     */
    List<InvitationEntity> findByHostIdAndStatusOrderByCreatedAtDesc(UUID hostId, InvitationStatus status);

    /**
     * Finds pending invitations that have expired.
     */
    @Query("SELECT i FROM InvitationEntity i WHERE i.status = 'PENDING' AND i.expiresAt < :now")
    List<InvitationEntity> findExpiredPending(@Param("now") LocalDateTime now);

    /**
     * Checks if a token already exists.
     */
    boolean existsByToken(String token);
}
