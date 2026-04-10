package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.AlertStatus;
import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.AlertRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.AlertEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.AlertPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.AlertJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing AlertRepositoryPort using JPA.
 */
@Component
public class AlertRepositoryAdapter implements AlertRepositoryPort {

    private final AlertJpaRepository jpaRepository;
    private final AlertPersistenceMapper mapper;

    public AlertRepositoryAdapter(AlertJpaRepository jpaRepository,
                                   AlertPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Alert save(Alert alert) {
        AlertEntity entity = mapper.toEntity(alert);
        AlertEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Alert> findById(AlertId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Alert> findByLinkId(LinkId linkId) {
        return jpaRepository.findByLinkIdOrderByCreatedAtDesc(linkId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findByLinkIdAndStatus(LinkId linkId, AlertStatus status) {
        return jpaRepository.findByLinkIdAndStatusOrderByCreatedAtDesc(linkId.getValue(), status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findPendingByHostId(UserId hostId) {
        return jpaRepository.findPendingByHostId(hostId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findByProtectedUserId(UserId protectedUserId) {
        return jpaRepository.findByProtectedUserIdOrderByCreatedAtDesc(protectedUserId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findPendingByProtectedUserId(UserId protectedUserId) {
        return jpaRepository.findByProtectedUserIdAndStatusOrderByCreatedAtDesc(
                        protectedUserId.getValue(), AlertStatus.PENDING)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countPendingByHostId(UserId hostId) {
        return jpaRepository.countPendingByHostId(hostId.getValue());
    }

    @Override
    public void delete(AlertId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsPendingByLinkIdAndUrl(LinkId linkId, String url) {
        return jpaRepository.existsByLinkIdAndSuspiciousUrlAndStatus(
                linkId.getValue(), url, AlertStatus.PENDING);
    }
}
