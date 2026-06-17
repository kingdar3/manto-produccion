package com.guardianapp.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InstalledApp {
    private final UUID id;
    private final String protectedUserId; // Cambiado a String
    private final String packageName;
    private final String appName;
    private final LocalDateTime reportedAt;

    public InstalledApp(UUID id, String protectedUserId, String packageName, String appName, LocalDateTime reportedAt) {
        this.id = id;
        this.protectedUserId = protectedUserId;
        this.packageName = packageName;
        this.appName = appName;
        this.reportedAt = reportedAt;
    }

    public UUID getId() {return id;}
    public String getProtectedUserId() {return protectedUserId;} // Cambiado a String
    public String getPackageName() {return packageName;}
    public String getAppName() {return appName;}
    public LocalDateTime getReportedAt() {return reportedAt;}
}