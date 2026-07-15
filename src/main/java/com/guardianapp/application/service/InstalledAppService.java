package com.guardianapp.application.service;

import com.guardianapp.domain.exception.FamilyGroupException;
import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.domain.model.ManagedInstalledApp;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.ReportInstalledAppsUseCase;
import com.guardianapp.domain.port.out.BlockedAppRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.InstalledAppRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InstalledAppService implements ReportInstalledAppsUseCase {

    private final InstalledAppRepositoryPort repositoryPort;
    private final BlockedAppRepositoryPort blockedAppRepositoryPort;
    private final FamilyGroupRepositoryPort familyGroupRepository;

    public InstalledAppService(
            InstalledAppRepositoryPort repositoryPort,
            BlockedAppRepositoryPort blockedAppRepositoryPort,
            FamilyGroupRepositoryPort familyGroupRepository) {
        this.repositoryPort = repositoryPort;
        this.blockedAppRepositoryPort = blockedAppRepositoryPort;
        this.familyGroupRepository = familyGroupRepository;
    }

    @Override
    public void reportApps(String protectedUserId, List<InstalledApp> apps) {
        repositoryPort.deleteAllByProtectedUserId(protectedUserId);
        repositoryPort.saveAll(apps);
    }

    @Override
    public List<InstalledApp> getInstalledApps(String protectedUserId) {
        return repositoryPort.findByProtectedUserId(protectedUserId);
    }

    @Override
    public List<ManagedInstalledApp> getManagedApps(String protectedUserId, String familyGroupId, String hostId) {
        FamilyGroup group = familyGroupRepository.findById(FamilyGroupId.fromString(familyGroupId))
                .orElseThrow(() -> FamilyGroupException.notFound(familyGroupId));

        UserId requesterId = UserId.fromString(hostId);
        if (!group.isHost(requesterId)) {
            throw FamilyGroupException.notAuthorized(hostId);
        }

        Map<String, BlockedApp> blockedByPackage = blockedAppRepositoryPort.findByFamilyGroupId(familyGroupId).stream()
                .collect(Collectors.toMap(BlockedApp::getPackageName, Function.identity()));

        return repositoryPort.findByProtectedUserId(protectedUserId).stream()
                .map(app -> {
                    BlockedApp blockedApp = blockedByPackage.get(app.getPackageName());
                    return new ManagedInstalledApp(
                            app.getId(),
                            blockedApp != null ? blockedApp.getId() : null,
                            app.getPackageName(),
                            app.getAppName(),
                            app.getReportedAt(),
                            blockedApp != null
                    );
                })
                .toList();
    }
}
