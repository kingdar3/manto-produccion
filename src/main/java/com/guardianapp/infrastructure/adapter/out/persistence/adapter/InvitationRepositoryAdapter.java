package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.InvitationRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.InvitationEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.InvitationPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.InvitationJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing InvitationRepositoryPort using JPA.
 */
@Component
public class InvitationRepositoryAdapter implements InvitationRepositoryPort {

    private final InvitationJpaRepository jpaRepository;
    private final InvitationPersistenceMapper mapper;

    public InvitationRepositoryAdapter(InvitationJpaRepository jpaRepository,
                                        InvitationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Invitation save(Invitation invitation) {
        InvitationEntity entity = mapper.toEntity(invitation);
        InvitationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Invitation> findById(InvitationId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Invitation> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public List<Invitation> findByHost(UserId hostId) {
        return jpaRepository.findByHostIdOrderByCreatedAtDesc(hostId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findByHostAndStatus(UserId hostId, InvitationStatus status) {
        return jpaRepository.findByHostIdAndStatusOrderByCreatedAtDesc(hostId.getValue(), status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findExpiredPending() {
        return jpaRepository.findExpiredPending(LocalDateTime.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(InvitationId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsByToken(String token) {
        return jpaRepository.existsByToken(token);
    }
}
