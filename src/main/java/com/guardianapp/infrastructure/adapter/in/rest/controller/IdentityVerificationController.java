package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;
import com.guardianapp.domain.port.in.IdentityVerificationUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateIdentityVerificationRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.RespondIdentityVerificationRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.IdentityVerificationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for identity verification flow.
 */
@RestController
@RequestMapping("/api/v1/identity-verifications")
public class IdentityVerificationController {

    private final IdentityVerificationUseCase useCase;

    public IdentityVerificationController(IdentityVerificationUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<IdentityVerificationResponse> create(
            @Valid @RequestBody CreateIdentityVerificationRequest request) {
        IdentityVerification verification = useCase.create(
            new IdentityVerificationUseCase.CreateVerificationCommand(
                com.guardianapp.domain.model.valueobject.LinkId.of(request.linkId()),
                UserId.of(request.protectedUserId()),
                request.claimedPerson()
            )
        );
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(IdentityVerificationResponse.from(verification));
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<IdentityVerificationResponse> respond(
            @PathVariable String id,
            @Valid @RequestBody RespondIdentityVerificationRequest request) {
        IdentityVerification verification = useCase.respond(
            new IdentityVerificationUseCase.RespondVerificationCommand(
                VerificationId.fromString(id),
                UserId.of(request.hostUserId()),
                request.approved(),
                request.note()
            )
        );
        return ResponseEntity.ok(IdentityVerificationResponse.from(verification));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityVerificationResponse> getById(@PathVariable String id) {
        return useCase.getById(VerificationId.fromString(id))
            .map(IdentityVerificationResponse::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<IdentityVerificationResponse>> getPendingByHost(
            @RequestHeader("X-User-Id") String hostId) {
        List<IdentityVerificationResponse> response = useCase.getPendingByHost(UserId.fromString(hostId))
            .stream()
            .map(IdentityVerificationResponse::from)
            .toList();
        return ResponseEntity.ok(response);
    }
}
