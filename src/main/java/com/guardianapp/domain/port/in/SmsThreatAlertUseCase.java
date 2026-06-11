package com.guardianapp.domain.port.in;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for SMS threat alert use cases.
 */
public interface SmsThreatAlertUseCase {

    SmsThreatAlert create(CreateSmsThreatAlertCommand command);

    Optional<SmsThreatAlert> getById(SmsThreatAlertId alertId);

    List<SmsThreatAlert> getPendingForHost(UserId hostId);

    SmsThreatAlert resolve(ResolveSmsThreatAlertCommand command);

    record CreateSmsThreatAlertCommand(
        LinkId linkId,
        UserId protectedUserId,
        String sender,
        String messageExcerpt,
        String detectedUrl,
        UrlThreatStatus analysisStatus,
        String analysisReason
    ) {
        public CreateSmsThreatAlertCommand {
            if (linkId == null) {
                throw new IllegalArgumentException("Link ID is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
            if (sender == null || sender.isBlank()) {
                throw new IllegalArgumentException("Sender is required");
            }
            if (messageExcerpt == null || messageExcerpt.isBlank()) {
                throw new IllegalArgumentException("Message excerpt is required");
            }
            if (detectedUrl == null || detectedUrl.isBlank()) {
                throw new IllegalArgumentException("Detected URL is required");
            }
            if (analysisStatus == null) {
                throw new IllegalArgumentException("Analysis status is required");
            }
        }
    }

    record ResolveSmsThreatAlertCommand(
        SmsThreatAlertId alertId,
        UserId hostId,
        boolean allowAccess,
        String note
    ) {
        public ResolveSmsThreatAlertCommand {
            if (alertId == null) {
                throw new IllegalArgumentException("Alert ID is required");
            }
            if (hostId == null) {
                throw new IllegalArgumentException("Host ID is required");
            }
        }
    }
}
