package com.guardianapp.infrastructure.adapter.out.persistence.repository;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.InstalledAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstalledAppJpaRepository extends JpaRepository<InstalledAppEntity, UUID> {
    List<InstalledAppEntity> findByProtectedUserId(String protectedUserId); // Cambiado a String
    void deleteByProtectedUserId(String protectedUserId); // Cambiado a String
}