package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.TrustedDomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for trusted domains whitelist.
 */
@Repository
public interface TrustedDomainJpaRepository extends JpaRepository<TrustedDomainEntity, UUID> {

    List<TrustedDomainEntity> findByActiveTrue();

    Optional<TrustedDomainEntity> findByDomainIgnoreCase(String domain);
}
