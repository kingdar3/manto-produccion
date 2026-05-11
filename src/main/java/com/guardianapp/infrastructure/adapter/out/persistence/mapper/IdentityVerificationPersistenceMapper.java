package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.IdentityVerificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for identity verification entity <-> domain model.
 */
@Mapper(componentModel = "spring")
public interface IdentityVerificationPersistenceMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "linkId", source = "linkId.value")
    @Mapping(target = "protectedUserId", source = "protectedUserId.value")
    @Mapping(target = "hostUserId", source = "hostUserId.value")
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "protectedUser", ignore = true)
    @Mapping(target = "hostUser", ignore = true)
    IdentityVerificationEntity toEntity(IdentityVerification verification);

    default IdentityVerification toDomain(IdentityVerificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return IdentityVerification.reconstitute(
            VerificationId.of(entity.getId()),
            LinkId.of(entity.getLinkId()),
            UserId.of(entity.getProtectedUserId()),
            UserId.of(entity.getHostUserId()),
            entity.getClaimedPerson(),
            entity.getChallengeCode(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getExpiresAt(),
            entity.getResolvedAt(),
            entity.getResolutionNote()
        );
    }
}
