package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;

/**
 * Input port for emergency history retrieval.
 */
public interface EmergencyHistoryUseCase {

    List<EmergencyAlert> getHistoryForHost(UserId hostId);
}
