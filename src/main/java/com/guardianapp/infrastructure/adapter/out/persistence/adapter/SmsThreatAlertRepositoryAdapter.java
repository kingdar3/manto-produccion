package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.SmsThreatAlertRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.SmsThreatAlertEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.SmsThreatAlertPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.SmsThreatAlertJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing SMS threat repository port using JPA.
 */
@Component
public class SmsThreatAlertRepositoryAdapter implements SmsThreatAlertRepositoryPort {

    private final SmsThreatAlertJpaRepository jpaRepository;
    private final SmsThreatAlertPersistenceMapper mapper;

    public SmsThreatAlertRepositoryAdapter(SmsThreatAlertJpaRepository jpaRepository,
                                           SmsThreatAlertPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SmsThreatAlert save(SmsThreatAlert alert) {
        SmsThreatAlertEntity entity = mapper.toEntity(alert);
        SmsThreatAlertEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SmsThreatAlert> findById(SmsThreatAlertId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<SmsThreatAlert> findPendingByHostId(UserId hostId) {
        return jpaRepository.findByHostUserIdAndStatusOrderByCreatedAtDesc(
                hostId.getValue(), SmsThreatAlertStatus.PENDING)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SmsThreatAlert> findByLinkIdAndStatus(LinkId linkId, SmsThreatAlertStatus status) {
        return jpaRepository.findByLinkIdAndStatusOrderByCreatedAtDesc(linkId.getValue(), status)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsPendingByLinkIdAndUrl(LinkId linkId, String detectedUrl) {
        return jpaRepository.existsByLinkIdAndDetectedUrlAndStatus(
            linkId.getValue(),
            detectedUrl,
            SmsThreatAlertStatus.PENDING
        );
    }
}
