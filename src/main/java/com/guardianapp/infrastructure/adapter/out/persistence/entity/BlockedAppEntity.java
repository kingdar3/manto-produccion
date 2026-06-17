package com.guardianapp.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blocked_apps")
public class BlockedAppEntity {

    @Id
    private UUID id;

    @Column(name = "family_group_id", nullable = false)
    private String familyGroupId; // Cambiado a String

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "app_name", nullable = false)
    private String appName;

    @Column(name = "blocked_by", nullable = false)
    private String blockedBy; // Cambiado a String

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BlockedAppEntity() {}

    public BlockedAppEntity(UUID id, String familyGroupId, String packageName, String appName, String blockedBy, LocalDateTime createdAt) {
        this.id = id;
        this.familyGroupId = familyGroupId;
        this.packageName = packageName;
        this.appName = appName;
        this.blockedBy = blockedBy;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFamilyGroupId() { return familyGroupId; } // Cambiado a String
    public void setFamilyGroupId(String familyGroupId) { this.familyGroupId = familyGroupId; } // Cambiado a String
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getBlockedBy() { return blockedBy; } // Cambiado a String
    public void setBlockedBy(String blockedBy) { this.blockedBy = blockedBy; } // Cambiado a String
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}