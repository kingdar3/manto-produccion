package com.guardianapp.infrastructure.adapter.out.persistence.adapter;

import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.FamilyGroupMember;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyGroupEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.entity.FamilyGroupMemberEntity;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.FamilyGroupJpaRepository;
import com.guardianapp.infrastructure.adapter.out.persistence.repository.FamilyGroupMemberJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing family group repository port using JPA.
 */
@Component
public class FamilyGroupRepositoryAdapter implements FamilyGroupRepositoryPort {

    private final FamilyGroupJpaRepository familyGroupJpaRepository;
    private final FamilyGroupMemberJpaRepository memberJpaRepository;

    public FamilyGroupRepositoryAdapter(
            FamilyGroupJpaRepository familyGroupJpaRepository,
            FamilyGroupMemberJpaRepository memberJpaRepository) {
        this.familyGroupJpaRepository = familyGroupJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    @Transactional
    public FamilyGroup save(FamilyGroup familyGroup) {
        FamilyGroupEntity groupEntity = FamilyGroupEntity.builder()
                .id(familyGroup.getId().getValue())
                .name(familyGroup.getName())
                .primaryHostUserId(familyGroup.getPrimaryHostUserId().getValue())
                .createdAt(familyGroup.getCreatedAt())
                .build();
        familyGroupJpaRepository.save(groupEntity);

        memberJpaRepository.deleteByFamilyGroupId(familyGroup.getId().getValue());
        // Ensure deletes hit the DB before we insert new rows.
        // Without this, Hibernate can flush inserts before deletes and trigger uk_family_group_user violations.
        memberJpaRepository.flush();
        Map<UUID, FamilyGroupMemberEntity> uniqueMembers = new LinkedHashMap<>();
        for (var member : familyGroup.getMembers()) {
            UUID userId = member.getUserId().getValue();
            uniqueMembers.putIfAbsent(userId, FamilyGroupMemberEntity.builder()
                    .id(UUID.randomUUID())
                    .familyGroupId(familyGroup.getId().getValue())
                    .userId(userId)
                    .role(member.getRole())
                    .joinedAt(member.getJoinedAt())
                    .build());
        }
        List<FamilyGroupMemberEntity> memberEntities = new ArrayList<>(uniqueMembers.values());
        memberJpaRepository.saveAll(memberEntities);

        return familyGroup;
    }

    @Override
    public Optional<FamilyGroup> findById(FamilyGroupId id) {
        return familyGroupJpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<FamilyGroup> findByUserId(UserId userId) {
        List<FamilyGroupMemberEntity> memberRows = memberJpaRepository.findByUserId(userId.getValue());
        Map<UUID, FamilyGroup> groups = new LinkedHashMap<>();
        for (FamilyGroupMemberEntity memberRow : memberRows) {
            familyGroupJpaRepository.findById(memberRow.getFamilyGroupId())
                    .map(this::toDomain)
                    .ifPresent(group -> groups.put(group.getId().getValue(), group));
        }
        return new ArrayList<>(groups.values());
    }

    @Override
    public List<FamilyGroup> findByPrimaryHostUserId(UserId userId) {
        return familyGroupJpaRepository.findByPrimaryHostUserId(userId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(FamilyGroupId id) {
        memberJpaRepository.deleteByFamilyGroupId(id.getValue());
        memberJpaRepository.flush();
        familyGroupJpaRepository.deleteById(id.getValue());
    }

    private FamilyGroup toDomain(FamilyGroupEntity entity) {
        List<FamilyGroupMember> members = memberJpaRepository.findByFamilyGroupId(entity.getId())
                .stream()
                .map(member -> FamilyGroupMember.reconstitute(
                        UserId.of(member.getUserId()),
                        member.getRole(),
                        member.getJoinedAt()
                ))
                .toList();

        return FamilyGroup.reconstitute(
                FamilyGroupId.of(entity.getId()),
                entity.getName(),
                UserId.of(entity.getPrimaryHostUserId()),
                entity.getCreatedAt(),
                members
        );
    }
}
