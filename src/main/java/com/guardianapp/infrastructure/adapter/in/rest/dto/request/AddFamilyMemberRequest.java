package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import com.guardianapp.domain.enums.FamilyMemberRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for adding a family member.
 */
public record AddFamilyMemberRequest(
        @NotNull(message = "Member user ID is required")
        UUID memberUserId,

        @NotNull(message = "Member role is required")
        FamilyMemberRole role
) {
}
