package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.SmsThreatAlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for SMS threat alert domain/entity conversion.
 */
@Mapper(componentModel = "spring")
public interface SmsThreatAlertPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "linkId", source = "linkId.value")
    @Mapping(target = "protectedUserId", source = "protectedUserId.value")
    @Mapping(target = "hostUserId", source = "hostUserId.value")
    @Mapping(target = "resolvedByUserId", source = "resolvedByUserId.value")
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "protectedUser", ignore = true)
    @Mapping(target = "hostUser", ignore = true)
    @Mapping(target = "resolvedByUser", ignore = true)
    SmsThreatAlertEntity toEntity(SmsThreatAlert alert);

    default SmsThreatAlert toDomain(SmsThreatAlertEntity entity) {
        if (entity == null) {
            return null;
        }
        return SmsThreatAlert.reconstitute(
            SmsThreatAlertId.of(entity.getId()),
            LinkId.of(entity.getLinkId()),
            UserId.of(entity.getProtectedUserId()),
            UserId.of(entity.getHostUserId()),
            entity.getSender(),
            entity.getMessageExcerpt(),
            entity.getDetectedUrl(),
            entity.getAnalysisStatus(),
            entity.getAnalysisReason(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getResolvedAt(),
            entity.getResolvedByUserId() != null ? UserId.of(entity.getResolvedByUserId()) : null,
            entity.getResolutionNote()
        );
    }
}
