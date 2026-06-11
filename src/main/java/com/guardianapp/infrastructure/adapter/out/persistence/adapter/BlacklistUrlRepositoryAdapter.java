package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.BlacklistUrl;
import com.guardianapp.domain.port.out.BlacklistUrlRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.BlacklistUrlEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.BlacklistUrlJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Persistence adapter for blacklist URL storage.
 */
@Component
public class BlacklistUrlRepositoryAdapter implements BlacklistUrlRepositoryPort {

    private final BlacklistUrlJpaRepository repository;

    public BlacklistUrlRepositoryAdapter(BlacklistUrlJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUrl(String url) {
        return repository.existsByUrlIgnoreCase(url);
    }

    @Override
    public BlacklistUrl save(String url) {
        BlacklistUrlEntity saved = repository.save(
            BlacklistUrlEntity.builder().url(url).build()
        );
        return new BlacklistUrl(saved.getId(), saved.getUrl(), saved.getCreatedAt());
    }

    @Override
    public void removeByUrl(String url) {
        repository.deleteByUrlIgnoreCase(url);
    }
}
