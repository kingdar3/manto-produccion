package com.guardianapp.domain.port.in;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.FamilyInvitation;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for family invitation operations.
 */
public interface FamilyInvitationUseCase {

    FamilyInvitation create(CreateFamilyInvitationCommand command);

    Optional<FamilyInvitation> getByToken(String token);

    FamilyGroup accept(AcceptFamilyInvitationCommand command);

    FamilyInvitation cancel(CancelFamilyInvitationCommand command);

    List<FamilyInvitation> getByFamilyGroup(FamilyGroupId familyGroupId);

    record CreateFamilyInvitationCommand(
            FamilyGroupId familyGroupId,
            UserId inviterUserId,
            FamilyMemberRole role
    ) {
        public CreateFamilyInvitationCommand {
            if (familyGroupId == null) {
                throw new IllegalArgumentException("Family group ID is required");
            }
            if (inviterUserId == null) {
                throw new IllegalArgumentException("Inviter user ID is required");
            }
            if (role == null) {
                throw new IllegalArgumentException("Family invitation target role is required");
            }
        }
    }

    record AcceptFamilyInvitationCommand(
            String token,
            UserId acceptedByUserId
    ) {
        public AcceptFamilyInvitationCommand {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is required");
            }
            if (acceptedByUserId == null) {
                throw new IllegalArgumentException("Accepted by user ID is required");
            }
        }
    }

    record CancelFamilyInvitationCommand(
            String token,
            UserId requesterUserId
    ) {
        public CancelFamilyInvitationCommand {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is required");
            }
            if (requesterUserId == null) {
                throw new IllegalArgumentException("Requester user ID is required");
            }
        }
    }
}
