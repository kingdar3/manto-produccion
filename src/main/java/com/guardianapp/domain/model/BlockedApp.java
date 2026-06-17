package com.guardianapp.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class BlockedApp {
    private final UUID id;
    private final String familyGroupId; // Cambiado a String
    private final String packageName;
    private final String appName;
    private final String blockedBy; // Cambiado a String
    private final LocalDateTime createdAt;

    // ¡Este es el constructor que debe tener los 6 parámetros!
    public BlockedApp(UUID id, String familyGroupId, String packageName, String appName, String blockedBy, LocalDateTime createdAt) {
        this.id = id;
        this.familyGroupId = familyGroupId;
        this.packageName = packageName;
        this.appName = appName;
        this.blockedBy = blockedBy;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getFamilyGroupId() { return familyGroupId; } // Cambiado a String
    public String getPackageName() { return packageName; }
    public String getAppName() { return appName; }
    public String getBlockedBy() { return blockedBy; } // Cambiado a String
    public LocalDateTime getCreatedAt() { return createdAt; }
}