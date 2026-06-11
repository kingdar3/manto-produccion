package com.guardianapp.infrastructure.config;

import com.guardianapp.infrastructure.adapter.out.persistence.entity.TrustedDomainEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.TrustedDomainJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Seeds trusted domains (banks and social networks).
 */
@Configuration
public class TrustedDomainSeedConfig {

    @Bean
    public CommandLineRunner seedTrustedDomains(TrustedDomainJpaRepository repository) {
        return args -> {
            seedIfMissing(repository, "bcp.com.pe", "Banco de Credito del Peru", "BANK");
            seedIfMissing(repository, "viabcp.com", "Banco de Credito del Peru", "BANK");
            seedIfMissing(repository, "bbva.pe", "BBVA Peru", "BANK");
            seedIfMissing(repository, "interbank.pe", "Interbank", "BANK");
            seedIfMissing(repository, "scotiabank.com.pe", "Scotiabank Peru", "BANK");
            seedIfMissing(repository, "banbif.com.pe", "BanBif", "BANK");
            seedIfMissing(repository, "pichincha.pe", "Banco Pichincha Peru", "BANK");

            seedIfMissing(repository, "facebook.com", "Facebook", "SOCIAL");
            seedIfMissing(repository, "instagram.com", "Instagram", "SOCIAL");
            seedIfMissing(repository, "whatsapp.com", "WhatsApp", "SOCIAL");
            seedIfMissing(repository, "x.com", "X", "SOCIAL");
            seedIfMissing(repository, "twitter.com", "X", "SOCIAL");
            seedIfMissing(repository, "linkedin.com", "LinkedIn", "SOCIAL");
            seedIfMissing(repository, "youtube.com", "YouTube", "SOCIAL");
            seedIfMissing(repository, "tiktok.com", "TikTok", "SOCIAL");
            seedIfMissing(repository, "telegram.org", "Telegram", "SOCIAL");
            seedIfMissing(repository, "reddit.com", "Reddit", "SOCIAL");
        };
    }

    private void seedIfMissing(TrustedDomainJpaRepository repository,
                               String domain,
                               String provider,
                               String category) {
        if (repository.findByDomainIgnoreCase(domain).isPresent()) {
            return;
        }

        repository.save(TrustedDomainEntity.builder()
            .domain(domain.toLowerCase())
            .providerName(provider)
            .category(category)
            .active(true)
            .build());
    }
}
