package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManagedInstalledAppResponse(
        UUID installedAppId,
        UUID blockedAppId,
        String packageName,
        String appName,
        LocalDateTime reportedAt,
        boolean blocked
) {}
