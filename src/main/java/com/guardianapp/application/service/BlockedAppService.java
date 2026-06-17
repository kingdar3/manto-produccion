package com.guardianapp.application.service;

import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.domain.port.in.BlockAppUseCase;
import com.guardianapp.domain.port.out.BlockedAppRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BlockedAppService implements BlockAppUseCase {

    private final BlockedAppRepositoryPort repositoryPort;

    public BlockedAppService(BlockedAppRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public BlockedApp blockApp(String familyGroupId, String packageName, String appName, String hostId) {
        // Validar si ya está bloqueada para no duplicar
        if (repositoryPort.existsByFamilyGroupIdAndPackageName(familyGroupId, packageName)) {
            throw new RuntimeException("Esta aplicación ya está bloqueada para tu grupo familiar.");
        }

        BlockedApp newBlock = new BlockedApp(
                UUID.randomUUID(), // Este sí se queda como UUID porque es el ID único del registro
                familyGroupId,
                packageName,
                appName,
                hostId,
                LocalDateTime.now()
        );

        return repositoryPort.save(newBlock);
    }

    @Override
    public void unblockApp(UUID blockedAppId) {
        repositoryPort.deleteById(blockedAppId);
    }

    @Override
    public List<BlockedApp> getBlockedAppsByFamilyGroup(String familyGroupId) {
        return repositoryPort.findByFamilyGroupId(familyGroupId);
    }
}