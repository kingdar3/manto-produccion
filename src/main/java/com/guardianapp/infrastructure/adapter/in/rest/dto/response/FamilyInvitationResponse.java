package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.FamilyInvitationStatus;
import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.FamilyInvitation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for family invitations.
 */
public record FamilyInvitationResponse(
        UUID id,
        UUID familyGroupId,
        UUID invitedByUserId,
        FamilyMemberRole targetRole,
        String token,
        FamilyInvitationStatus status,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt,
        UUID acceptedByUserId
) {
    public static FamilyInvitationResponse from(FamilyInvitation invitation) {
        return new FamilyInvitationResponse(
                invitation.getId().getValue(),
                invitation.getFamilyGroupId().getValue(),
                invitation.getInvitedByUserId().getValue(),
                invitation.getTargetRole(),
                invitation.getToken(),
                invitation.getStatus(),
                invitation.getExpiresAt(),
                invitation.getCreatedAt(),
                invitation.getAcceptedAt(),
                invitation.getAcceptedByUserId() != null ? invitation.getAcceptedByUserId().getValue() : null
        );
    }
}
