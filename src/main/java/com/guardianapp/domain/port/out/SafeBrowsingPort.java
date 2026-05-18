package com.guardianapp.domain.port.out;

import com.guardianapp.domain.enums.UrlThreatStatus;

import java.util.List;
import java.util.Map;

/**
 * Output port for Google Safe Browsing integration.
 */
public interface SafeBrowsingPort {

    /**
     * Returns threat status for each URL passed.
     */
    Map<String, UrlThreatStatus> checkUrls(List<String> urls);
}
