package com.guardianapp.application.service;

import com.guardianapp.domain.exception.BlockedAppException;
import com.guardianapp.domain.exception.FamilyGroupException;
import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.BlockAppUseCase;
import com.guardianapp.domain.port.out.BlockedAppRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BlockedAppService implements BlockAppUseCase {

    private final BlockedAppRepositoryPort repositoryPort;
    private final FamilyGroupRepositoryPort familyGroupRepository;

    public BlockedAppService(
            BlockedAppRepositoryPort repositoryPort,
            FamilyGroupRepositoryPort familyGroupRepository) {
        this.repositoryPort = repositoryPort;
        this.familyGroupRepository = familyGroupRepository;
    }

    @Override
    public BlockedApp blockApp(String familyGroupId, String packageName, String appName, String hostId) {
        FamilyGroup familyGroup = getAuthorizedFamilyGroup(familyGroupId, hostId);

        if (repositoryPort.existsByFamilyGroupIdAndPackageName(familyGroupId, packageName)) {
            throw BlockedAppException.alreadyBlocked(packageName);
        }

        BlockedApp newBlock = new BlockedApp(
                UUID.randomUUID(),
                familyGroup.getId().toString(),
                packageName,
                appName,
                hostId,
                LocalDateTime.now()
        );

        return repositoryPort.save(newBlock);
    }

    @Override
    public void unblockApp(UUID blockedAppId, String hostId) {
        BlockedApp blockedApp = repositoryPort.findById(blockedAppId)
                .orElseThrow(() -> BlockedAppException.notFound(blockedAppId.toString()));

        getAuthorizedFamilyGroup(blockedApp.getFamilyGroupId(), hostId);
        repositoryPort.deleteById(blockedAppId);
    }

    @Override
    public List<BlockedApp> getBlockedAppsByFamilyGroup(String familyGroupId) {
        return repositoryPort.findByFamilyGroupId(familyGroupId);
    }

    private FamilyGroup getAuthorizedFamilyGroup(String familyGroupId, String hostId) {
        FamilyGroup group = familyGroupRepository.findById(FamilyGroupId.fromString(familyGroupId))
                .orElseThrow(() -> FamilyGroupException.notFound(familyGroupId));

        UserId requesterId = UserId.fromString(hostId);
        if (!group.isHost(requesterId)) {
            throw BlockedAppException.notAuthorized(hostId);
        }

        return group;
    }
}
