package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.enums.LinkStatus;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.LinkPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.LinkJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the LinkRepositoryPort output port
 * using JPA for persistence.
 */
@Component
public class LinkRepositoryAdapter implements LinkRepositoryPort {

    private final LinkJpaRepository jpaRepository;
    private final LinkPersistenceMapper mapper;

    public LinkRepositoryAdapter(LinkJpaRepository jpaRepository, 
                                 LinkPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Link save(Link link) {
        var entity = mapper.toEntity(link);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Link> findById(LinkId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Link> findByHost(UserId hostId) {
        return jpaRepository.findByHostId(hostId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Link> findByProtected(UserId protectedId) {
        return jpaRepository.findByProtectedId(protectedId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Link> findByUser(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Link> findByHostAndProtected(UserId hostId, UserId protectedId) {
        return jpaRepository.findByHostIdAndProtectedId(
                hostId.getValue(), 
                protectedId.getValue()
            )
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsActiveOrPending(UserId hostId, UserId protectedId) {
        return jpaRepository.existsActiveOrPending(
            hostId.getValue(), 
            protectedId.getValue()
        );
    }

    @Override
    public List<Link> findByStatus(LinkStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Link> findActiveByHost(UserId hostId) {
        return jpaRepository.findByHostIdAndStatus(
                hostId.getValue(), 
                LinkStatus.ACTIVE
            )
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Link> findActiveByProtected(UserId protectedId) {
        return jpaRepository.findByProtectedIdAndStatus(
                protectedId.getValue(),
                LinkStatus.ACTIVE
            )
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void delete(LinkId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
