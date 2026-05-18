package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.ThreatAnalysisResult;

import java.util.List;

/**
 * Input port for threat analysis.
 */
public interface AnalyzeThreatUseCase {

    ThreatAnalysisResult analyze(AnalyzeThreatCommand command);

    /**
     * Command to analyze extracted URLs from messages.
     */
    record AnalyzeThreatCommand(
        String message,
        List<String> urls,
        String sender
    ) {
        public AnalyzeThreatCommand {
            if (urls == null || urls.isEmpty()) {
                throw new IllegalArgumentException("At least one URL is required");
            }
        }
    }
}
