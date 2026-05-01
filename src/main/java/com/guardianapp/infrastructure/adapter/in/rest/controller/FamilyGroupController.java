package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.FamilyGroupUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.AddFamilyMemberRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateFamilyGroupRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.FamilyGroupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for family group operations.
 */
@RestController
@RequestMapping("/api/v1/families")
public class FamilyGroupController {

    private final FamilyGroupUseCase familyGroupUseCase;

    public FamilyGroupController(FamilyGroupUseCase familyGroupUseCase) {
        this.familyGroupUseCase = familyGroupUseCase;
    }

    @PostMapping
    public ResponseEntity<FamilyGroupResponse> createFamilyGroup(
            @RequestHeader("X-User-Id") String requesterUserId,
            @Valid @RequestBody CreateFamilyGroupRequest request) {

        FamilyGroup group = familyGroupUseCase.create(
                new FamilyGroupUseCase.CreateFamilyGroupCommand(
                        request.name(),
                        UserId.fromString(requesterUserId)
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(FamilyGroupResponse.from(group));
    }

    @GetMapping("/{familyId}")
    public ResponseEntity<FamilyGroupResponse> getFamilyGroup(@PathVariable String familyId) {
        return familyGroupUseCase.getById(FamilyGroupId.fromString(familyId))
                .map(group -> ResponseEntity.ok(FamilyGroupResponse.from(group)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<FamilyGroupResponse>> getMyFamilyGroups(
            @RequestHeader("X-User-Id") String requesterUserId) {

        List<FamilyGroupResponse> response = familyGroupUseCase.getByUser(UserId.fromString(requesterUserId))
                .stream()
                .map(FamilyGroupResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{familyId}/members")
    public ResponseEntity<FamilyGroupResponse> addMember(
            @PathVariable String familyId,
            @RequestHeader("X-User-Id") String requesterUserId,
            @Valid @RequestBody AddFamilyMemberRequest request) {

        FamilyGroup group = familyGroupUseCase.addMember(
                new FamilyGroupUseCase.AddFamilyMemberCommand(
                        FamilyGroupId.fromString(familyId),
                        UserId.fromString(requesterUserId),
                        UserId.of(request.memberUserId()),
                        request.role()
                )
        );
        return ResponseEntity.ok(FamilyGroupResponse.from(group));
    }

    @DeleteMapping("/{familyId}/members/{memberUserId}")
    public ResponseEntity<FamilyGroupResponse> removeMember(
            @PathVariable String familyId,
            @PathVariable String memberUserId,
            @RequestHeader("X-User-Id") String requesterUserId) {

        FamilyGroup group = familyGroupUseCase.removeMember(
                new FamilyGroupUseCase.RemoveFamilyMemberCommand(
                        FamilyGroupId.fromString(familyId),
                        UserId.fromString(requesterUserId),
                        UserId.fromString(memberUserId)
                )
        );
        return ResponseEntity.ok(FamilyGroupResponse.from(group));
    }
}
