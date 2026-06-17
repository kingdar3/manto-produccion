package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import java.util.List;

public record ReportInstalledAppsRequest(List<AppInfo> apps) {
    public record AppInfo(String packageName, String appName) {}
}