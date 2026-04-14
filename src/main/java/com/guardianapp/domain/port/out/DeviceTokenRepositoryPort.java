package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;

/**
 * Output port for managing device tokens used by push notifications.
 */
public interface DeviceTokenRepositoryPort {

    void saveOrUpdate(UserId userId, String token, String platform);

    List<String> findTokensByUserId(UserId userId);
}
