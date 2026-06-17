package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.BlockedApp;
import java.util.List;
import java.util.UUID;

public interface BlockedAppRepositoryPort {
    BlockedApp save(BlockedApp blockedApp);
    void deleteById(UUID id); // El ID interno sigue siendo UUID
    List<BlockedApp> findByFamilyGroupId(String familyGroupId); // Cambiado a String
    boolean existsByFamilyGroupIdAndPackageName(String familyGroupId, String packageName); // Cambiado a String
}