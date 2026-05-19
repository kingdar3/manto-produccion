package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.BlacklistUrl;

/**
 * Input port for blacklist URL operations.
 */
public interface BlacklistUrlUseCase {

    BlacklistUrl register(RegisterBlacklistUrlCommand command);

    record RegisterBlacklistUrlCommand(String url) {
        public RegisterBlacklistUrlCommand {
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("URL is required");
            }
        }
    }
}
