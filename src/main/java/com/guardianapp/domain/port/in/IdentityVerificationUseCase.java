package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for identity verification operations.
 */
public interface IdentityVerificationUseCase {

    IdentityVerification create(CreateVerificationCommand command);

    IdentityVerification respond(RespondVerificationCommand command);

    Optional<IdentityVerification> getById(VerificationId verificationId);

    List<IdentityVerification> getPendingByHost(UserId hostId);

    record CreateVerificationCommand(
        com.guardianapp.domain.model.valueobject.LinkId linkId,
        UserId protectedUserId,
        String claimedPerson
    ) {
        public CreateVerificationCommand {
            if (linkId == null) {
                throw new IllegalArgumentException("Link ID is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
            if (claimedPerson == null || claimedPerson.isBlank()) {
                throw new IllegalArgumentException("Claimed person is required");
            }
        }
    }

    record RespondVerificationCommand(
        VerificationId verificationId,
        UserId hostUserId,
        boolean approved,
        String note
    ) {
        public RespondVerificationCommand {
            if (verificationId == null) {
                throw new IllegalArgumentException("Verification ID is required");
            }
            if (hostUserId == null) {
                throw new IllegalArgumentException("Host user ID is required");
            }
        }
    }
}
