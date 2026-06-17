package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.BlockedApp;
import java.util.List;
import java.util.UUID;

public interface BlockAppUseCase {
    BlockedApp blockApp(String familyGroupId, String packageName, String appName, String hostId);
    void unblockApp(UUID blockedAppId);
    List<BlockedApp> getBlockedAppsByFamilyGroup(String familyGroupId);
}