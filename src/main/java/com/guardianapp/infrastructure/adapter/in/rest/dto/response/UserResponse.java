package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for user data.
 * Note: No role field - users can be both HOST and PROTECTED in different links.
 */
public record UserResponse(
    String id,
    String name,
    String email,
    String phone,
    LocalDateTime createdAt,
    boolean active
) {}
