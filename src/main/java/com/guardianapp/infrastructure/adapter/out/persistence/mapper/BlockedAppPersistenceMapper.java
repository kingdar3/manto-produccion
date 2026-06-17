package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.BlockedAppEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlockedAppPersistenceMapper {
    BlockedApp toDomain(BlockedAppEntity entity);
    BlockedAppEntity toEntity(BlockedApp domain);
}