package com.guardianapp.infrastructure.adapter.out.persistence.mapper;

import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Mapper to convert between User domain model and UserEntity JPA entity.
 */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "userIdToUuid")
    UserEntity toEntity(User user);

    default User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.reconstruct(
            UserId.of(entity.getId()),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getCreatedAt(),
            entity.isActive()
        );
    }

    @Named("userIdToUuid")
    default UUID userIdToUuid(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }
}
