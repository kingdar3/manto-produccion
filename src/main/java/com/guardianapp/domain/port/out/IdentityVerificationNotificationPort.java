package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.IdentityVerification;

/**
 * Output port for realtime and push notifications related to identity verification.
 */
public interface IdentityVerificationNotificationPort {

    void notifyCreated(IdentityVerification verification);

    void notifyResolved(IdentityVerification verification);
}
