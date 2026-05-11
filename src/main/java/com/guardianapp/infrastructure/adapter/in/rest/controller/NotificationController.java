package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.application.service.NotificationTokenService;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.RegisterDeviceTokenRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for push notification token registration.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationTokenService notificationTokenService;

    public NotificationController(NotificationTokenService notificationTokenService) {
        this.notificationTokenService = notificationTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<Void> registerDeviceToken(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody RegisterDeviceTokenRequest request) {
        notificationTokenService.register(UserId.fromString(userId), request.token(), request.platform());
        return ResponseEntity.ok().build();
    }
}
