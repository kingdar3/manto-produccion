package com.guardianapp.infrastructure.adapter.out.safebrowsing.mapper;

import com.guardianapp.domain.enums.UrlThreatStatus;
import org.springframework.stereotype.Component;

/**
 * Maps Google threat types to internal threat statuses.
 */
@Component
public class SafeBrowsingThreatMapper {

    public UrlThreatStatus toStatus(String threatType) {
        if (threatType == null || threatType.isBlank()) {
            return UrlThreatStatus.SUSPICIOUS;
        }

        return switch (threatType) {
            case "SOCIAL_ENGINEERING" -> UrlThreatStatus.PHISHING;
            case "MALWARE" -> UrlThreatStatus.MALWARE;
            case "UNWANTED_SOFTWARE" -> UrlThreatStatus.UNWANTED;
            case "POTENTIALLY_HARMFUL_APPLICATION" -> UrlThreatStatus.SUSPICIOUS;
            default -> UrlThreatStatus.SUSPICIOUS;
        };
    }
}
