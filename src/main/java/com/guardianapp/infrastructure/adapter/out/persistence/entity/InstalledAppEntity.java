package com.guardianapp.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "protected_installed_apps")
public class InstalledAppEntity {

    @Id
    private UUID id;

    @Column(name = "protected_user_id", nullable = false)
    private String protectedUserId; // Cambiado a String

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "app_name", nullable = false)
    private String appName;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    public InstalledAppEntity() {}

    public InstalledAppEntity(UUID id, String protectedUserId, String packageName, String appName, LocalDateTime reportedAt) {
        this.id = id;
        this.protectedUserId = protectedUserId;
        this.packageName = packageName;
        this.appName = appName;
        this.reportedAt = reportedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProtectedUserId() { return protectedUserId; } // Cambiado a String
    public void setProtectedUserId(String protectedUserId) { this.protectedUserId = protectedUserId; } // Cambiado a String
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
}