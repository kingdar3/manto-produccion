package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstalledAppResponse(
        UUID id,
        String packageName,
        String appName,
        LocalDateTime reportedAt
) {}