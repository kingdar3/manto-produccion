package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.SmsThreatAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for SMS threat alerts.
 */
@Repository
public interface SmsThreatAlertJpaRepository extends JpaRepository<SmsThreatAlertEntity, UUID> {

    List<SmsThreatAlertEntity> findByHostUserIdAndStatusOrderByCreatedAtDesc(UUID hostUserId,
                                                                              SmsThreatAlertStatus status);

    List<SmsThreatAlertEntity> findByLinkIdAndStatusOrderByCreatedAtDesc(UUID linkId,
                                                                          SmsThreatAlertStatus status);

    boolean existsByLinkIdAndDetectedUrlAndStatus(UUID linkId,
                                                   String detectedUrl,
                                                   SmsThreatAlertStatus status);
}
