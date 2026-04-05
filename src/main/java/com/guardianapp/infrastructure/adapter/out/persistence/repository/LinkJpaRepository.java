package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.LinkStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for link database operations.
 */
@Repository
public interface LinkJpaRepository extends JpaRepository<LinkEntity, UUID> {

    List<LinkEntity> findByHostId(UUID hostId);

    List<LinkEntity> findByProtectedId(UUID protectedId);

    @Query("SELECT l FROM LinkEntity l WHERE l.hostId = :userId OR l.protectedId = :userId")
    List<LinkEntity> findByUserId(@Param("userId") UUID userId);

    List<LinkEntity> findByHostIdAndProtectedId(UUID hostId, UUID protectedId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
           "FROM LinkEntity l " +
           "WHERE l.hostId = :hostId " +
           "AND l.protectedId = :protectedId " +
           "AND l.status IN ('ACTIVE', 'PENDING')")
    boolean existsActiveOrPending(@Param("hostId") UUID hostId, 
                                  @Param("protectedId") UUID protectedId);

    List<LinkEntity> findByStatus(LinkStatus status);

    List<LinkEntity> findByHostIdAndStatus(UUID hostId, LinkStatus status);
}
