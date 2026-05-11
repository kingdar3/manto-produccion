package com.guardianapp.infrastructure.adapter.in.websocket;

import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raw WebSocket handler for realtime emergency audio streaming.
 *
 * Protocol:
 * - Connect: /ws/emergency-audio?emergencyId=...&role=host|protected
 * - Protected sends binary PCM chunks
 * - Server relays each binary chunk to all host sessions in same emergency
 */
@Component
public class EmergencyAudioWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EmergencyAudioWebSocketHandler.class);
    private static final int MAX_CHUNK_BYTES = 64 * 1024;

    private final Map<String, Set<WebSocketSession>> hostSessionsByEmergency = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> allSessionsByEmergency = new ConcurrentHashMap<>();
    private final Map<String, String> emergencyBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> roleBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> userIdBySessionId = new ConcurrentHashMap<>();

    private final EmergencyAlertRepositoryPort emergencyAlertRepository;
    private final LinkRepositoryPort linkRepository;
    private final FamilyGroupRepositoryPort familyGroupRepository;

    public EmergencyAudioWebSocketHandler(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            LinkRepositoryPort linkRepository,
            FamilyGroupRepositoryPort familyGroupRepository) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.linkRepository = linkRepository;
        this.familyGroupRepository = familyGroupRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        QueryParams params = extractParams(session.getUri());
        if (params.emergencyId == null || params.emergencyId.isBlank() || params.userId == null || params.userId.isBlank()) {
            session.sendMessage(new TextMessage("ERROR: emergencyId and userId are required"));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        EmergencyAlert alert;
        UserId userId;
        try {
            alert = emergencyAlertRepository.findById(EmergencyAlertId.fromString(params.emergencyId)).orElse(null);
            userId = UserId.fromString(params.userId);
        } catch (Exception ex) {
            session.sendMessage(new TextMessage("ERROR: invalid emergencyId or userId"));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (alert == null || !alert.isActive()) {
            session.sendMessage(new TextMessage("ERROR: emergency is not active"));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        String role = params.role != null ? params.role : "host";
        if (!isAuthorized(alert, role, userId)) {
            session.sendMessage(new TextMessage("ERROR: not authorized for this emergency stream"));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        emergencyBySessionId.put(session.getId(), params.emergencyId);
        roleBySessionId.put(session.getId(), role);
        userIdBySessionId.put(session.getId(), params.userId);
        allSessionsByEmergency.computeIfAbsent(params.emergencyId, key -> ConcurrentHashMap.newKeySet()).add(session);

        if ("host".equalsIgnoreCase(role)) {
            hostSessionsByEmergency
                    .computeIfAbsent(params.emergencyId, key -> ConcurrentHashMap.newKeySet())
                    .add(session);
        }

        session.sendMessage(new TextMessage("CONNECTED:" + params.emergencyId + ":" + role));
        log.debug("Emergency audio WS connected: session={}, emergency={}, role={}", session.getId(), params.emergencyId, role);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String emergencyId = emergencyBySessionId.get(session.getId());
        String role = roleBySessionId.get(session.getId());

        if (emergencyId == null || !"protected".equalsIgnoreCase(role)) {
            return;
        }

        if (message.getPayloadLength() > MAX_CHUNK_BYTES) {
            return;
        }

        Set<WebSocketSession> hosts = hostSessionsByEmergency.get(emergencyId);
        if (hosts == null || hosts.isEmpty()) {
            return;
        }

        for (WebSocketSession hostSession : hosts) {
            if (hostSession.isOpen()) {
                hostSession.sendMessage(new BinaryMessage(message.getPayload().asReadOnlyBuffer()));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("ACK"));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String emergencyId = emergencyBySessionId.remove(session.getId());
        String role = roleBySessionId.remove(session.getId());
        userIdBySessionId.remove(session.getId());

        if (emergencyId != null && "host".equalsIgnoreCase(role)) {
            Set<WebSocketSession> hosts = hostSessionsByEmergency.get(emergencyId);
            if (hosts != null) {
                hosts.remove(session);
                if (hosts.isEmpty()) {
                    hostSessionsByEmergency.remove(emergencyId);
                }
            }
        }

        if (emergencyId != null) {
            Set<WebSocketSession> allSessions = allSessionsByEmergency.get(emergencyId);
            if (allSessions != null) {
                allSessions.remove(session);
                if (allSessions.isEmpty()) {
                    allSessionsByEmergency.remove(emergencyId);
                }
            }
        }

        log.debug("Emergency audio WS disconnected: session={}, status={}", session.getId(), status);
    }

    public void closeSessionsForEmergency(String emergencyId) {
        Set<WebSocketSession> sessions = allSessionsByEmergency.remove(emergencyId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.NORMAL.withReason("Emergency resolved"));
                }
            } catch (Exception ignored) {
            }
            emergencyBySessionId.remove(session.getId());
            roleBySessionId.remove(session.getId());
            userIdBySessionId.remove(session.getId());
        }
        hostSessionsByEmergency.remove(emergencyId);
    }

    private boolean isAuthorized(EmergencyAlert alert, String role, UserId userId) {
        if ("protected".equalsIgnoreCase(role)) {
            return alert.getProtectedUserId().equals(userId);
        }
        if (alert.getPrimaryHostUserId().equals(userId)) {
            return true;
        }

        for (FamilyGroup group : familyGroupRepository.findByUserId(alert.getProtectedUserId())) {
            if (group.getProtectedUserIds().stream().anyMatch(id -> id.equals(alert.getProtectedUserId()))
                    && group.isHost(userId)) {
                return true;
            }
        }

        for (Link link : linkRepository.findByProtected(alert.getProtectedUserId())) {
            if (link.isActive() && link.getHostId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private QueryParams extractParams(URI uri) {
        if (uri == null || uri.getQuery() == null || uri.getQuery().isBlank()) {
            return new QueryParams(null, null, null);
        }
        String emergencyId = null;
        String role = null;
        String userId = null;

        for (String pair : uri.getQuery().split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            if ("emergencyId".equalsIgnoreCase(parts[0])) {
                emergencyId = parts[1];
            } else if ("role".equalsIgnoreCase(parts[0])) {
                role = parts[1];
            } else if ("userId".equalsIgnoreCase(parts[0])) {
                userId = parts[1];
            }
        }
        return new QueryParams(emergencyId, role, userId);
    }

    private record QueryParams(String emergencyId, String role, String userId) {
    }
}
