package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.InvitationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * MapStruct mapper for Invitation entity <-> domain model.
 */
@Mapper(componentModel = "spring")
public interface InvitationPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "hostId", source = "hostId.value")
    @Mapping(target = "acceptedByUserId", source = "acceptedByUserId.value")
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "acceptedByUser", ignore = true)
    InvitationEntity toEntity(Invitation invitation);

    @Named("toDomain")
    default Invitation toDomain(InvitationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Invitation.reconstitute(
            InvitationId.of(entity.getId()),
            UserId.of(entity.getHostId()),
            entity.getToken(),
            entity.getHostName(),
            entity.getStatus(),
            entity.getExpiresAt(),
            entity.getCreatedAt(),
            entity.getAcceptedAt(),
            entity.getAcceptedByUserId() != null ? UserId.of(entity.getAcceptedByUserId()) : null
        );
    }
}
