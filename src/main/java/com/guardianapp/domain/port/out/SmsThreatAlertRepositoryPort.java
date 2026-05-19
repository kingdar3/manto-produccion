package com.guardianapp.domain.port.out;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for SMS threat alert persistence.
 */
public interface SmsThreatAlertRepositoryPort {

    SmsThreatAlert save(SmsThreatAlert alert);

    Optional<SmsThreatAlert> findById(SmsThreatAlertId id);

    List<SmsThreatAlert> findPendingByHostId(UserId hostId);

    List<SmsThreatAlert> findByLinkIdAndStatus(LinkId linkId, SmsThreatAlertStatus status);

    boolean existsPendingByLinkIdAndUrl(LinkId linkId, String detectedUrl);
}
