package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.AlertStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for alerts.
 */
@Repository
public interface AlertJpaRepository extends JpaRepository<AlertEntity, UUID> {

    /**
     * Finds all alerts for a specific link.
     */
    List<AlertEntity> findByLinkIdOrderByCreatedAtDesc(UUID linkId);

    /**
     * Finds alerts by link and status.
     */
    List<AlertEntity> findByLinkIdAndStatusOrderByCreatedAtDesc(UUID linkId, AlertStatus status);

    /**
     * Finds all pending alerts for links where the given user is host.
     * Joins with links table to filter by host.
     */
    @Query("SELECT a FROM AlertEntity a " +
           "JOIN LinkEntity l ON a.linkId = l.id " +
           "WHERE l.hostId = :hostId AND a.status = 'PENDING' " +
           "ORDER BY a.createdAt DESC")
    List<AlertEntity> findPendingByHostId(@Param("hostId") UUID hostId);

    /**
     * Finds all alerts for a protected user.
     */
    List<AlertEntity> findByProtectedUserIdOrderByCreatedAtDesc(UUID protectedUserId);

    /**
     * Finds pending alerts for a protected user.
     */
    List<AlertEntity> findByProtectedUserIdAndStatusOrderByCreatedAtDesc(
        UUID protectedUserId, AlertStatus status);

    /**
     * Counts pending alerts for a host user.
     */
    @Query("SELECT COUNT(a) FROM AlertEntity a " +
           "JOIN LinkEntity l ON a.linkId = l.id " +
           "WHERE l.hostId = :hostId AND a.status = 'PENDING'")
    long countPendingByHostId(@Param("hostId") UUID hostId);

    /**
     * Checks if a pending alert exists for the same URL in a link.
     */
    boolean existsByLinkIdAndSuspiciousUrlAndStatus(UUID linkId, String suspiciousUrl, AlertStatus status);
}
