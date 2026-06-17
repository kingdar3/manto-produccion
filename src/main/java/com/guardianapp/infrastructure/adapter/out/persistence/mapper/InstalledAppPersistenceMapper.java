package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.InstalledAppEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstalledAppPersistenceMapper {
    InstalledApp toDomain(InstalledAppEntity entity);
    InstalledAppEntity toEntity(InstalledApp domain);
}