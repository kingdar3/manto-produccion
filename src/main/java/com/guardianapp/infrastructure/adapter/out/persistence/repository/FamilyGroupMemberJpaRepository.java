package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyGroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for family group members.
 */
@Repository
public interface FamilyGroupMemberJpaRepository extends JpaRepository<FamilyGroupMemberEntity, UUID> {

    List<FamilyGroupMemberEntity> findByFamilyGroupId(UUID familyGroupId);

    List<FamilyGroupMemberEntity> findByUserId(UUID userId);

    void deleteByFamilyGroupId(UUID familyGroupId);
}
