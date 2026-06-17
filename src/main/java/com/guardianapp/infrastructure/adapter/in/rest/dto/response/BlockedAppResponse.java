package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BlockedAppResponse(
        UUID id,
        String familyGroupId,
        String packageName,
        String appName,
        LocalDateTime createdAt
) {}