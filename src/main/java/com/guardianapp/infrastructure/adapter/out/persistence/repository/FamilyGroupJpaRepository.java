package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for family groups.
 */
@Repository
public interface FamilyGroupJpaRepository extends JpaRepository<FamilyGroupEntity, UUID> {

    List<FamilyGroupEntity> findByPrimaryHostUserId(UUID primaryHostUserId);
}
