package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.TrustedDomainMatch;
import com.guardianapp.domain.port.out.TrustedDomainRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.TrustedDomainEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.TrustedDomainJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Adapter for trusted domain whitelist checks.
 */
@Component
public class TrustedDomainRepositoryAdapter implements TrustedDomainRepositoryPort {

    private final TrustedDomainJpaRepository repository;

    public TrustedDomainRepositoryAdapter(TrustedDomainJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TrustedDomainMatch> findMatchForHost(String host) {
        if (host == null || host.isBlank()) {
            return Optional.empty();
        }

        String normalizedHost = host.toLowerCase();
        List<TrustedDomainEntity> allTrusted = repository.findByActiveTrue();

        return allTrusted.stream()
            .filter(entry -> {
                String domain = entry.getDomain().toLowerCase();
                return normalizedHost.equals(domain) || normalizedHost.endsWith("." + domain);
            })
            .max(Comparator.comparingInt(entry -> entry.getDomain().length()))
            .map(entry -> new TrustedDomainMatch(
                entry.getDomain(),
                entry.getProviderName(),
                entry.getCategory()
            ));
    }
}
