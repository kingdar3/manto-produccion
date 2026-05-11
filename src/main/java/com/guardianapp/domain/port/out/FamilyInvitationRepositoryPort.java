package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.FamilyInvitation;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.FamilyInvitationId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for family invitation persistence.
 */
public interface FamilyInvitationRepositoryPort {

    FamilyInvitation save(FamilyInvitation invitation);

    Optional<FamilyInvitation> findById(FamilyInvitationId invitationId);

    Optional<FamilyInvitation> findByToken(String token);

    List<FamilyInvitation> findByFamilyGroupId(FamilyGroupId familyGroupId);

    List<FamilyInvitation> findPendingByInviter(UserId inviterUserId);
}
