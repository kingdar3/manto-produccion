package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.DeviceTokenEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.DeviceTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Adapter implementing device token repository port.
 */
@Component
public class DeviceTokenRepositoryAdapter implements DeviceTokenRepositoryPort {

    private final DeviceTokenJpaRepository jpaRepository;

    public DeviceTokenRepositoryAdapter(DeviceTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void saveOrUpdate(UserId userId, String token, String platform) {
        DeviceTokenEntity entity = jpaRepository.findByToken(token)
            .orElseGet(() -> DeviceTokenEntity.builder().id(UUID.randomUUID()).token(token).build());

        entity.setUserId(userId.getValue());
        entity.setPlatform(platform);
        jpaRepository.save(entity);
    }

    @Override
    public List<String> findTokensByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
            .stream()
            .map(DeviceTokenEntity::getToken)
            .toList();
    }
}
