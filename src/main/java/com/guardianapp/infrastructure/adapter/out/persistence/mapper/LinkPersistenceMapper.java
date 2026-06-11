package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Mapper to convert between Link domain model and LinkEntity JPA entity.
 */
@Mapper(componentModel = "spring")
public interface LinkPersistenceMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "linkIdToUuid")
    @Mapping(target = "hostId", source = "hostId", qualifiedByName = "userIdToUuid")
    @Mapping(target = "protectedId", source = "protectedId", qualifiedByName = "userIdToUuid")
    LinkEntity toEntity(Link link);

    default Link toDomain(LinkEntity entity) {
        if (entity == null) {
            return null;
        }

        return Link.reconstruct(
            LinkId.of(entity.getId()),
            UserId.of(entity.getHostId()),
            UserId.of(entity.getProtectedId()),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getConfirmedAt(),
            entity.getUpdatedAt()
        );
    }

    @Named("linkIdToUuid")
    default UUID linkIdToUuid(LinkId linkId) {
        return linkId != null ? linkId.getValue() : null;
    }

    @Named("userIdToUuid")
    default UUID userIdToUuid(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }
}
