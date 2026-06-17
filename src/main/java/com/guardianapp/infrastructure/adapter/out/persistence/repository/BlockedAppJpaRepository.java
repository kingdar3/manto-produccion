package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.BlockedAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockedAppJpaRepository extends JpaRepository<BlockedAppEntity, UUID> {
    List<BlockedAppEntity> findByFamilyGroupId(String familyGroupId); // Cambiado a String
    boolean existsByFamilyGroupIdAndPackageName(String familyGroupId, String packageName); // Cambiado a String
}