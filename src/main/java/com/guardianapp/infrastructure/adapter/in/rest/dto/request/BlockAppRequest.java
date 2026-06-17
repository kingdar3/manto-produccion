package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import java.util.UUID;

public record BlockAppRequest(
        String familyGroupId,
        String packageName,
        String appName
) {}