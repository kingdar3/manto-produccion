package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for identity verification persistence.
 */
public interface IdentityVerificationRepositoryPort {

    IdentityVerification save(IdentityVerification verification);

    Optional<IdentityVerification> findById(VerificationId id);

    List<IdentityVerification> findPendingByHost(UserId hostId);

    List<IdentityVerification> findByProtected(UserId protectedUserId);
}
