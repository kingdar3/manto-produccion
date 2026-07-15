package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.domain.port.out.BlockedAppRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.mapper.BlockedAppPersistenceMapper;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.BlockedAppJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BlockedAppRepositoryAdapter implements BlockedAppRepositoryPort {

    private final BlockedAppJpaRepository jpaRepository;
    private final BlockedAppPersistenceMapper mapper;

    public BlockedAppRepositoryAdapter(BlockedAppJpaRepository jpaRepository, BlockedAppPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public BlockedApp save(BlockedApp blockedApp) {
        var entity = mapper.toEntity(blockedApp);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<BlockedApp> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<BlockedApp> findByFamilyGroupId(String familyGroupId) { // Cambiado a String
        return jpaRepository.findByFamilyGroupId(familyGroupId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByFamilyGroupIdAndPackageName(String familyGroupId, String packageName) { // Cambiado a String
        return jpaRepository.existsByFamilyGroupIdAndPackageName(familyGroupId, packageName);
    }
}
