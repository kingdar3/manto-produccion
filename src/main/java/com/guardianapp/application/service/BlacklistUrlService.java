package com.guardianapp.application.service;

import com.guardianapp.domain.exception.BlacklistUrlException;
import com.guardianapp.domain.model.BlacklistUrl;
import com.guardianapp.domain.port.in.BlacklistUrlUseCase;
import com.guardianapp.domain.port.out.BlacklistUrlRepositoryPort;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Application service for blacklist URL registration.
 */
public class BlacklistUrlService implements BlacklistUrlUseCase {

    private final BlacklistUrlRepositoryPort repository;

    public BlacklistUrlService(BlacklistUrlRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public BlacklistUrl register(RegisterBlacklistUrlCommand command) {
        String normalizedUrl = normalize(command.url());
        if (!isValidHttpUrl(normalizedUrl)) {
            throw BlacklistUrlException.invalidUrl(normalizedUrl);
        }

        if (repository.existsByUrl(normalizedUrl)) {
            throw BlacklistUrlException.alreadyExists(normalizedUrl);
        }

        return repository.save(normalizedUrl);
    }

    private String normalize(String url) {
        return url == null ? "" : url.trim();
    }

    private boolean isValidHttpUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                && uri.getHost() != null
                && !uri.getHost().isBlank();
        } catch (URISyntaxException ex) {
            return false;
        }
    }
}
