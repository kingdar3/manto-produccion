package com.guardianapp.application.service;

import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.domain.port.in.ReportInstalledAppsUseCase;
import com.guardianapp.domain.port.out.InstalledAppRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstalledAppService implements ReportInstalledAppsUseCase {

    private final InstalledAppRepositoryPort repositoryPort;

    public InstalledAppService(InstalledAppRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
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
}