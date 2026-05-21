package com.guardianapp.application.service;

import com.guardianapp.domain.exception.BlacklistUrlException;
import com.guardianapp.domain.model.BlacklistUrl;
import com.guardianapp.domain.port.in.BlacklistUrlUseCase.RegisterBlacklistUrlCommand;
import com.guardianapp.domain.port.out.BlacklistUrlRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlacklistUrlServiceTest {

    @Test
    void shouldRegisterBlacklistedUrl() {
        InMemoryRepo repo = new InMemoryRepo();
        BlacklistUrlService service = new BlacklistUrlService(repo);

        BlacklistUrl created = service.register(
            new RegisterBlacklistUrlCommand("http://malicious-example.xyz")
        );

        assertEquals("http://malicious-example.xyz", created.url());
    }

    @Test
    void shouldRejectDuplicateUrl() {
        InMemoryRepo repo = new InMemoryRepo();
        repo.urls.add("http://malicious-example.xyz");
        BlacklistUrlService service = new BlacklistUrlService(repo);

        assertThrows(BlacklistUrlException.class, () -> service.register(
            new RegisterBlacklistUrlCommand("http://malicious-example.xyz")
        ));
    }

    @Test
    void shouldRejectInvalidUrl() {
        InMemoryRepo repo = new InMemoryRepo();
        BlacklistUrlService service = new BlacklistUrlService(repo);

        assertThrows(BlacklistUrlException.class, () -> service.register(
            new RegisterBlacklistUrlCommand("not-a-url")
        ));
    }

    private static class InMemoryRepo implements BlacklistUrlRepositoryPort {
        private final Set<String> urls = new HashSet<>();

        @Override
        public boolean existsByUrl(String url) {
            return urls.stream().anyMatch(saved -> saved.equalsIgnoreCase(url));
        }

        @Override
        public BlacklistUrl save(String url) {
            urls.add(url);
            return new BlacklistUrl(UUID.randomUUID(), url, LocalDateTime.now());
        }

        @Override
        public void removeByUrl(String url) {
            urls.removeIf(saved -> saved.equalsIgnoreCase(url));
        }
    }
}