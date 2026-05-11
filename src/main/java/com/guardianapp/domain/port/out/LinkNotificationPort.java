package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.Link;

/**
 * Output port for link lifecycle notifications.
 */
public interface LinkNotificationPort {

    void notifyLinkPending(Link link);

    void notifyLinkActivated(Link link);
}
