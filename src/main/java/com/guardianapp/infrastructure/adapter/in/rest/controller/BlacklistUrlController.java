package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.BlacklistUrl;
import com.guardianapp.domain.port.in.BlacklistUrlUseCase;
import com.guardianapp.domain.port.in.BlacklistUrlUseCase.RegisterBlacklistUrlCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.RegisterBlacklistUrlRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.BlacklistUrlResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for blacklist URL registration.
 */
@RestController
@RequestMapping("/api/v1/blacklist/urls")
public class BlacklistUrlController {

    private final BlacklistUrlUseCase blacklistUrlUseCase;

    public BlacklistUrlController(BlacklistUrlUseCase blacklistUrlUseCase) {
        this.blacklistUrlUseCase = blacklistUrlUseCase;
    }

    /**
     * Registers a URL in the blacklist table.
     *
     * POST /api/v1/blacklist/urls
     */
    @PostMapping
    public ResponseEntity<BlacklistUrlResponse> register(@Valid @RequestBody RegisterBlacklistUrlRequest request) {
        BlacklistUrl created = blacklistUrlUseCase.register(new RegisterBlacklistUrlCommand(request.url()));
        return ResponseEntity.status(HttpStatus.CREATED).body(BlacklistUrlResponse.from(created));
    }

    /**
     * Removes a URL from the blacklist table.
     *
     * DELETE /api/v1/blacklist/urls
     */
    @DeleteMapping
    public ResponseEntity<Void> remove(@Valid @RequestBody RegisterBlacklistUrlRequest request) {
        blacklistUrlUseCase.remove(new BlacklistUrlUseCase.RemoveBlacklistUrlCommand(request.url()));
        return ResponseEntity.noContent().build();
    }
}
