package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.domain.port.out.InstalledAppRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.InstalledAppPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.InstalledAppJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstalledAppRepositoryAdapter implements InstalledAppRepositoryPort {

    private final InstalledAppJpaRepository jpaRepository;
    private final InstalledAppPersistenceMapper mapper;

    public InstalledAppRepositoryAdapter(InstalledAppJpaRepository jpaRepository, InstalledAppPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void saveAll(List<InstalledApp> apps) {
        var entities = apps.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<InstalledApp> findByProtectedUserId(String protectedUserId) { // Cambiado a String
        return jpaRepository.findByProtectedUserId(protectedUserId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllByProtectedUserId(String protectedUserId) { // Cambiado a String
        jpaRepository.deleteByProtectedUserId(protectedUserId);
    }
}