package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.BlacklistUrl;

/**
 * Output port for blacklist URL persistence.
 */
public interface BlacklistUrlRepositoryPort {

    boolean existsByUrl(String url);

    BlacklistUrl save(String url);
}
