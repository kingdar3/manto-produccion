package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.EmergencyAlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for emergency alert entity/domain conversions.
 */
@Mapper(componentModel = "spring")
public interface EmergencyAlertPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "linkId", source = "linkId.value")
    @Mapping(target = "protectedUserId", source = "protectedUserId.value")
    @Mapping(target = "primaryHostUserId", source = "primaryHostUserId.value")
    @Mapping(target = "resolvedByUserId", source = "resolvedByUserId.value")
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "protectedUser", ignore = true)
    @Mapping(target = "primaryHostUser", ignore = true)
    @Mapping(target = "resolvedByUser", ignore = true)
    EmergencyAlertEntity toEntity(EmergencyAlert emergencyAlert);

    default EmergencyAlert toDomain(EmergencyAlertEntity entity) {
        if (entity == null) {
            return null;
        }

        return EmergencyAlert.reconstitute(
                EmergencyAlertId.of(entity.getId()),
                LinkId.of(entity.getLinkId()),
                UserId.of(entity.getProtectedUserId()),
                UserId.of(entity.getPrimaryHostUserId()),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getResolvedAt(),
                entity.getResolvedByUserId() != null ? UserId.of(entity.getResolvedByUserId()) : null,
                entity.getResolutionType(),
                entity.getResolutionNote()
        );
    }
}
