package com.guardianapp.infrastructure.adapter.in.rest.mapper;

import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.ThreatAnalysisResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.UrlThreatAnalysisResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps threat analysis domain models to REST responses.
 */
@Component
public class ThreatAnalysisRestMapper {

    public ThreatAnalysisResponse toResponse(ThreatAnalysisResult result) {
        List<UrlThreatAnalysisResponse> urlResults = result.urlResults().stream()
            .map(UrlThreatAnalysisResponse::from)
            .toList();

        return new ThreatAnalysisResponse(
            result.status(),
            result.source(),
            result.detectedAt(),
            result.totalUrls(),
            result.analyzedUrls(),
            result.invalidUrls(),
            urlResults
        );
    }
}
