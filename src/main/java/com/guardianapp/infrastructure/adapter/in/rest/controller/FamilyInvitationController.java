package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.FamilyInvitation;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.FamilyInvitationUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateFamilyInvitationRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.FamilyGroupResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.FamilyInvitationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for family invitation operations.
 */
@RestController
@RequestMapping("/api/v1/family-invitations")
public class FamilyInvitationController {

    private final FamilyInvitationUseCase familyInvitationUseCase;

    public FamilyInvitationController(FamilyInvitationUseCase familyInvitationUseCase) {
        this.familyInvitationUseCase = familyInvitationUseCase;
    }

    @PostMapping("/families/{familyId}")
    public ResponseEntity<FamilyInvitationResponse> createInvitation(
            @PathVariable String familyId,
            @RequestHeader("X-User-Id") String requesterUserId,
            @Valid @RequestBody CreateFamilyInvitationRequest request) {

        FamilyInvitation invitation = familyInvitationUseCase.create(
                new FamilyInvitationUseCase.CreateFamilyInvitationCommand(
                        FamilyGroupId.fromString(familyId),
                        UserId.fromString(requesterUserId),
                        request.targetRole()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(FamilyInvitationResponse.from(invitation));
    }

    @GetMapping("/{token}")
    public ResponseEntity<FamilyInvitationResponse> getByToken(@PathVariable String token) {
        return familyInvitationUseCase.getByToken(token)
                .map(invitation -> ResponseEntity.ok(FamilyInvitationResponse.from(invitation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{token}/accept")
    public ResponseEntity<FamilyGroupResponse> acceptInvitation(
            @PathVariable String token,
            @RequestHeader("X-User-Id") String acceptedByUserId) {

        FamilyGroup group = familyInvitationUseCase.accept(
                new FamilyInvitationUseCase.AcceptFamilyInvitationCommand(
                        token,
                        UserId.fromString(acceptedByUserId)
                )
        );

        return ResponseEntity.ok(FamilyGroupResponse.from(group));
    }

    @PostMapping("/{token}/cancel")
    public ResponseEntity<FamilyInvitationResponse> cancelInvitation(
            @PathVariable String token,
            @RequestHeader("X-User-Id") String requesterUserId) {

        FamilyInvitation invitation = familyInvitationUseCase.cancel(
                new FamilyInvitationUseCase.CancelFamilyInvitationCommand(
                        token,
                        UserId.fromString(requesterUserId)
                )
        );
        return ResponseEntity.ok(FamilyInvitationResponse.from(invitation));
    }

    @GetMapping("/families/{familyId}")
    public ResponseEntity<List<FamilyInvitationResponse>> getByFamilyGroup(@PathVariable String familyId) {
        List<FamilyInvitationResponse> response = familyInvitationUseCase.getByFamilyGroup(FamilyGroupId.fromString(familyId))
                .stream()
                .map(FamilyInvitationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
