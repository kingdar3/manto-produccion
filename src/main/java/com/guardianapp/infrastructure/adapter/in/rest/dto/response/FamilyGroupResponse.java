package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.FamilyGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for family groups.
 */
public record FamilyGroupResponse(
        UUID id,
        String name,
        UUID primaryHostUserId,
        LocalDateTime createdAt,
        List<MemberResponse> members
) {
    public static FamilyGroupResponse from(FamilyGroup group) {
        return new FamilyGroupResponse(
                group.getId().getValue(),
                group.getName(),
                group.getPrimaryHostUserId().getValue(),
                group.getCreatedAt(),
                group.getMembers().stream()
                        .map(member -> new MemberResponse(
                                member.getUserId().getValue(),
                                member.getRole(),
                                member.getJoinedAt()
                        ))
                        .toList()
        );
    }

    public record MemberResponse(
            UUID userId,
            FamilyMemberRole role,
            LocalDateTime joinedAt
    ) {
    }
}
