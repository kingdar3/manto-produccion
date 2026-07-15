package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.domain.model.ManagedInstalledApp;
import java.util.List;

public interface ReportInstalledAppsUseCase {
    void reportApps(String protectedUserId, List<InstalledApp> apps);
    List<InstalledApp> getInstalledApps(String protectedUserId);
    List<ManagedInstalledApp> getManagedApps(String protectedUserId, String familyGroupId, String hostId);
}
