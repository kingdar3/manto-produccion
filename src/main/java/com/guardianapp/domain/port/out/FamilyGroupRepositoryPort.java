package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for family group persistence.
 */
public interface FamilyGroupRepositoryPort {

    FamilyGroup save(FamilyGroup familyGroup);

    Optional<FamilyGroup> findById(FamilyGroupId id);

    List<FamilyGroup> findByUserId(UserId userId);

    List<FamilyGroup> findByPrimaryHostUserId(UserId userId);
}
