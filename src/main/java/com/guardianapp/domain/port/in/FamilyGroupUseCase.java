package com.guardianapp.domain.port.in;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for family group operations.
 */
public interface FamilyGroupUseCase {

    FamilyGroup create(CreateFamilyGroupCommand command);

    FamilyGroup addMember(AddFamilyMemberCommand command);

    FamilyGroup removeMember(RemoveFamilyMemberCommand command);

    Optional<FamilyGroup> getById(FamilyGroupId familyGroupId);

    List<FamilyGroup> getByUser(UserId userId);

    record CreateFamilyGroupCommand(
            String name,
            UserId primaryHostUserId
    ) {
        public CreateFamilyGroupCommand {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Family group name is required");
            }
            if (primaryHostUserId == null) {
                throw new IllegalArgumentException("Primary host user ID is required");
            }
        }
    }

    record AddFamilyMemberCommand(
            FamilyGroupId familyGroupId,
            UserId requesterUserId,
            UserId memberUserId,
            FamilyMemberRole role
    ) {
        public AddFamilyMemberCommand {
            if (familyGroupId == null) {
                throw new IllegalArgumentException("Family group ID is required");
            }
            if (requesterUserId == null) {
                throw new IllegalArgumentException("Requester user ID is required");
            }
            if (memberUserId == null) {
                throw new IllegalArgumentException("Member user ID is required");
            }
            if (role == null) {
                throw new IllegalArgumentException("Role is required");
            }
            if (role == FamilyMemberRole.PRIMARY_HOST) {
                throw new IllegalArgumentException("Cannot add another primary host");
            }
        }
    }

    record RemoveFamilyMemberCommand(
            FamilyGroupId familyGroupId,
            UserId requesterUserId,
            UserId memberUserId
    ) {
        public RemoveFamilyMemberCommand {
            if (familyGroupId == null) {
                throw new IllegalArgumentException("Family group ID is required");
            }
            if (requesterUserId == null) {
                throw new IllegalArgumentException("Requester user ID is required");
            }
            if (memberUserId == null) {
                throw new IllegalArgumentException("Member user ID is required");
            }
        }
    }
}
