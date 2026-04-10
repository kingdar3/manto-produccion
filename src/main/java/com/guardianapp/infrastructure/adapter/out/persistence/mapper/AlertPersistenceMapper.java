package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.AlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Alert entity <-> domain model.
 */
@Mapper(componentModel = "spring")
public interface AlertPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "linkId", source = "linkId.value")
    @Mapping(target = "protectedUserId", source = "protectedUserId.value")
    @Mapping(target = "resolvedByUserId", source = "resolvedByUserId.value")
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "protectedUser", ignore = true)
    @Mapping(target = "resolvedByUser", ignore = true)
    AlertEntity toEntity(Alert alert);

    @Named("toDomain")
    default Alert toDomain(AlertEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Alert.reconstitute(
            AlertId.of(entity.getId()),
            LinkId.of(entity.getLinkId()),
            UserId.of(entity.getProtectedUserId()),
            entity.getSuspiciousUrl(),
            entity.getReason(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getResolvedAt(),
            entity.getResolvedByUserId() != null ? UserId.of(entity.getResolvedByUserId()) : null,
            entity.getResolutionNote()
        );
    }
}
