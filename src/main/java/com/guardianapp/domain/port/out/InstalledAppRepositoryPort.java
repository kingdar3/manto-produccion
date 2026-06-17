package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.InstalledApp;
import java.util.List;

public interface InstalledAppRepositoryPort {
    void saveAll(List<InstalledApp> apps);
    List<InstalledApp> findByProtectedUserId(String protectedUserId); // Cambiado a String
    void deleteAllByProtectedUserId(String protectedUserId); // Cambiado a String
}