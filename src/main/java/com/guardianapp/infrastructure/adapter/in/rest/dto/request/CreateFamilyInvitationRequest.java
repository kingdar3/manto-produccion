package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import com.guardianapp.domain.enums.FamilyMemberRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a family invitation.
 */
public record CreateFamilyInvitationRequest(
        @NotNull(message = "Target role is required")
        FamilyMemberRole targetRole
) {
}
