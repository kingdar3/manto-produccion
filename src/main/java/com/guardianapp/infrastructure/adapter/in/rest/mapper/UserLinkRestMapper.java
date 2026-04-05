package com.guardianapp.infrastructure.adapter.in.rest.mapper;

import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.Link;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.InvitationResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.UserResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.LinkResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper to convert domain models to REST response DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserLinkRestMapper {

    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserResponse toResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    @Mapping(target = "id", expression = "java(link.getId().toString())")
    @Mapping(target = "hostId", expression = "java(link.getHostId().toString())")
    @Mapping(target = "protectedId", expression = "java(link.getProtectedId().toString())")
    @Mapping(target = "connectionCode", expression = "java(link.getConnectionCode().getCode())")
    @Mapping(target = "codeExpiresAt", expression = "java(link.getConnectionCode().getExpiresAt())")
    @Mapping(target = "remainingMinutes", expression = "java(link.getConnectionCode().remainingMinutes())")
    LinkResponse toResponse(Link link);

    List<LinkResponse> toLinkResponseList(List<Link> links);

    @Named("toInvitationResponse")
    default InvitationResponse toResponse(Invitation invitation) {
        if (invitation == null) {
            return null;
        }
        return new InvitationResponse(
            invitation.getId().toString(),
            invitation.getHostId().toString(),
            invitation.getHostName(),
            invitation.getToken(),
            invitation.getShareableLink(),
            invitation.getStatus(),
            invitation.getExpiresAt(),
            invitation.getRemainingMinutes(),
            invitation.getCreatedAt(),
            invitation.getAcceptedAt(),
            invitation.getAcceptedByUserId() != null ? invitation.getAcceptedByUserId().toString() : null
        );
    }

    default List<InvitationResponse> toInvitationResponseList(List<Invitation> invitations) {
        return invitations.stream()
            .map(this::toResponse)
            .toList();
    }
}
