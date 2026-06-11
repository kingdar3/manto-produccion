package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.BlacklistUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * JPA repository for blacklist URLs.
 */
@Repository
public interface BlacklistUrlJpaRepository extends JpaRepository<BlacklistUrlEntity, UUID> {

    boolean existsByUrlIgnoreCase(String url);

    void deleteByUrlIgnoreCase(String url);
}
