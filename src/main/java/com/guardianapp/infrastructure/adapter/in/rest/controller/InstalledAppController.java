package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.InstalledApp;
import com.guardianapp.domain.model.ManagedInstalledApp;
import com.guardianapp.domain.port.in.ReportInstalledAppsUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.ReportInstalledAppsRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.InstalledAppResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.ManagedInstalledAppResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/installed-apps")
public class InstalledAppController {

    private final ReportInstalledAppsUseCase useCase;

    public InstalledAppController(ReportInstalledAppsUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/report")
    public ResponseEntity<Void> reportApps(
            @RequestHeader("X-User-Id") String protectedUserId,
            @RequestBody ReportInstalledAppsRequest request) {

        List<InstalledApp> domainApps = request.apps().stream()
                .map(app -> new InstalledApp(
                        UUID.randomUUID(),
                        protectedUserId,
                        app.packageName(),
                        app.appName(),
                        LocalDateTime.now()
                )).collect(Collectors.toList());

        useCase.reportApps(protectedUserId, domainApps);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{protectedUserId}")
    public ResponseEntity<List<InstalledAppResponse>> getInstalledApps(
            @RequestHeader("X-User-Id") String hostId,
            @PathVariable String protectedUserId) {

        List<InstalledAppResponse> response = useCase.getInstalledApps(protectedUserId).stream()
                .map(app -> new InstalledAppResponse(
                        app.getId(),
                        app.getPackageName(),
                        app.getAppName(),
                        app.getReportedAt()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{protectedUserId}/family/{familyGroupId}")
    public ResponseEntity<List<ManagedInstalledAppResponse>> getManagedInstalledApps(
            @RequestHeader("X-User-Id") String hostId,
            @PathVariable String protectedUserId,
            @PathVariable String familyGroupId) {

        List<ManagedInstalledAppResponse> response = useCase
                .getManagedApps(protectedUserId, familyGroupId, hostId)
                .stream()
                .map(this::mapToManagedResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private ManagedInstalledAppResponse mapToManagedResponse(ManagedInstalledApp app) {
        return new ManagedInstalledAppResponse(
                app.getInstalledAppId(),
                app.getBlockedAppId(),
                app.getPackageName(),
                app.getAppName(),
                app.getReportedAt(),
                app.isBlocked()
        );
    }
}
