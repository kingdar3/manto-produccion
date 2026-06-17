package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.BlockedApp;
import com.guardianapp.domain.port.in.BlockAppUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.BlockAppRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.BlockedAppResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blocked-apps")
public class BlockedAppController {

    private final BlockAppUseCase useCase;

    public BlockedAppController(BlockAppUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<BlockedAppResponse> blockApp(
            @RequestHeader("X-User-Id") String hostId, // Cambiado a String
            @RequestBody BlockAppRequest request) {

        BlockedApp blockedApp = useCase.blockApp(
                request.familyGroupId(),
                request.packageName(),
                request.appName(),
                hostId
        );

        return ResponseEntity.ok(mapToResponse(blockedApp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unblockApp(
            @RequestHeader("X-User-Id") String hostId, // Cambiado a String
            @PathVariable UUID id) { // El ID del bloqueo (interno) sí puede seguir siendo UUID
        useCase.unblockApp(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/family/{familyGroupId}")
    public ResponseEntity<List<BlockedAppResponse>> getBlockedAppsByFamily(
            @RequestHeader("X-User-Id") String hostId, // Cambiado a String
            @PathVariable String familyGroupId) { // Cambiado a String

        List<BlockedAppResponse> response = useCase.getBlockedAppsByFamilyGroup(familyGroupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-restrictions/{familyGroupId}")
    public ResponseEntity<List<BlockedAppResponse>> getMyRestrictions(
            @RequestHeader("X-User-Id") String protectedUserId, // Cambiado a String
            @PathVariable String familyGroupId) { // Cambiado a String

        List<BlockedAppResponse> response = useCase.getBlockedAppsByFamilyGroup(familyGroupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private BlockedAppResponse mapToResponse(BlockedApp app) {
        return new BlockedAppResponse(
                app.getId(),
                app.getFamilyGroupId(),
                app.getPackageName(),
                app.getAppName(),
                app.getCreatedAt()
        );
    }
}