package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.port.in.CreateLinkUseCase;
import com.guardianapp.domain.port.in.CreateLinkUseCase.ConfirmLinkCommand;
import com.guardianapp.domain.port.in.CreateLinkUseCase.CreateLinkCommand;
import com.guardianapp.domain.port.in.QueryLinksUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.ConfirmLinkRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateLinkRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.LinkResponse;
import com.guardianapp.infrastructure.adapter.in.rest.mapper.UserLinkRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for link operations.
 */
@RestController
@RequestMapping("/api/v1/links")
public class LinkController {

    private final CreateLinkUseCase createLinkUseCase;
    private final QueryLinksUseCase queryLinksUseCase;
    private final UserLinkRestMapper restMapper;

    public LinkController(CreateLinkUseCase createLinkUseCase,
                          QueryLinksUseCase queryLinksUseCase,
                          UserLinkRestMapper restMapper) {
        this.createLinkUseCase = createLinkUseCase;
        this.queryLinksUseCase = queryLinksUseCase;
        this.restMapper = restMapper;
    }

    /**
     * Gets all links for the current user.
     * 
     * GET /api/v1/links
     * Header: X-User-Id
     */
    @GetMapping
    public ResponseEntity<List<LinkResponse>> getMyLinks(
            @RequestHeader("X-User-Id") String userId) {
        
        List<Link> links = queryLinksUseCase.getMyLinks(UserId.fromString(userId));
        List<LinkResponse> response = restMapper.toLinkResponseList(links);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a link by ID.
     * 
     * GET /api/v1/links/{id}
     */
    @GetMapping("/{linkId}")
    public ResponseEntity<LinkResponse> getById(@PathVariable String linkId) {
        return queryLinksUseCase.getById(LinkId.fromString(linkId))
            .map(link -> ResponseEntity.ok(restMapper.toResponse(link)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets active links for the current user.
     * 
     * GET /api/v1/links/active
     * Header: X-User-Id
     */
    @GetMapping("/active")
    public ResponseEntity<List<LinkResponse>> getActiveLinks(
            @RequestHeader("X-User-Id") String userId) {
        
        List<Link> links = queryLinksUseCase.getActiveLinks(UserId.fromString(userId));
        List<LinkResponse> response = restMapper.toLinkResponseList(links);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets pending links for the current user.
     * 
     * GET /api/v1/links/pending
     * Header: X-User-Id
     */
    @GetMapping("/pending")
    public ResponseEntity<List<LinkResponse>> getPendingLinks(
            @RequestHeader("X-User-Id") String userId) {
        
        List<Link> links = queryLinksUseCase.getPendingLinks(UserId.fromString(userId));
        List<LinkResponse> response = restMapper.toLinkResponseList(links);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new link request.
     * The host initiates the request and receives a code to share with the protected user.
     * 
     * POST /api/v1/links
     * Header: X-User-Id (Host ID)
     */
    @PostMapping
    public ResponseEntity<LinkResponse> createRequest(
            @RequestHeader("X-User-Id") String hostId,
            @Valid @RequestBody CreateLinkRequest request) {

        CreateLinkCommand command = new CreateLinkCommand(
            UserId.fromString(hostId),
            UserId.fromString(request.protectedId())
        );

        Link link = createLinkUseCase.createRequest(command);
        LinkResponse response = restMapper.toResponse(link);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirms a link using the connection code.
     * The protected user enters the code to activate the link.
     * 
     * POST /api/v1/links/{linkId}/confirm
     * Header: X-User-Id (Protected user ID)
     */
    @PostMapping("/{linkId}/confirm")
    public ResponseEntity<LinkResponse> confirm(
            @PathVariable String linkId,
            @RequestHeader("X-User-Id") String protectedId,
            @Valid @RequestBody ConfirmLinkRequest request) {

        ConfirmLinkCommand command = new ConfirmLinkCommand(
            LinkId.fromString(linkId),
            UserId.fromString(protectedId),
            request.connectionCode()
        );

        Link link = createLinkUseCase.confirm(command);
        LinkResponse response = restMapper.toResponse(link);

        return ResponseEntity.ok(response);
    }

    /**
     * Rejects a link request.
     * 
     * POST /api/v1/links/{linkId}/reject
     * Header: X-User-Id (Protected user ID)
     */
    @PostMapping("/{linkId}/reject")
    public ResponseEntity<LinkResponse> reject(
            @PathVariable String linkId,
            @RequestHeader("X-User-Id") String protectedId) {

        Link link = createLinkUseCase.reject(
            LinkId.fromString(linkId),
            UserId.fromString(protectedId)
        );

        LinkResponse response = restMapper.toResponse(link);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an active or pending link.
     * 
     * POST /api/v1/links/{linkId}/cancel
     * Header: X-User-Id (ID of the user cancelling)
     */
    @PostMapping("/{linkId}/cancel")
    public ResponseEntity<LinkResponse> cancel(
            @PathVariable String linkId,
            @RequestHeader("X-User-Id") String userId) {

        Link link = createLinkUseCase.cancel(
            LinkId.fromString(linkId),
            UserId.fromString(userId)
        );

        LinkResponse response = restMapper.toResponse(link);
        return ResponseEntity.ok(response);
    }
}
