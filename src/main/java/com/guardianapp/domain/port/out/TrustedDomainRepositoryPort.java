package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.TrustedDomainMatch;

import java.util.Optional;

/**
 * Output port for trusted domains whitelist.
 */
public interface TrustedDomainRepositoryPort {

    /**
     * Returns a trusted match for a host if it belongs to the whitelist.
     */
    Optional<TrustedDomainMatch> findMatchForHost(String host);
}
