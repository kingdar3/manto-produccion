package com.guardianapp.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Installed app plus its current block state for a family group.
 */
public class ManagedInstalledApp {

    private final UUID installedAppId;
    private final UUID blockedAppId;
    private final String packageName;
    private final String appName;
    private final LocalDateTime reportedAt;
    private final boolean blocked;

    public ManagedInstalledApp(
            UUID installedAppId,
            UUID blockedAppId,
            String packageName,
            String appName,
            LocalDateTime reportedAt,
            boolean blocked) {
        this.installedAppId = installedAppId;
        this.blockedAppId = blockedAppId;
        this.packageName = packageName;
        this.appName = appName;
        this.reportedAt = reportedAt;
        this.blocked = blocked;
    }

    public UUID getInstalledAppId() {
        return installedAppId;
    }

    public UUID getBlockedAppId() {
        return blockedAppId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
