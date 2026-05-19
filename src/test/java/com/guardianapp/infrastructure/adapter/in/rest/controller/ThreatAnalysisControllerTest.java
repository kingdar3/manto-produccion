package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.model.UrlThreatAnalysis;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.mapper.ThreatAnalysisRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ThreatAnalysisController.class)
@Import({ThreatAnalysisRestMapper.class, ThreatAnalysisControllerTest.StubConfig.class})
class ThreatAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAnalysisResult() throws Exception {
        String payload = """
            {
              "message": "BBVA: su cuenta fue bloqueada...",
              "urls": ["http://bbva-seguridad.xyz"],
              "sender": "+51999999999"
            }
            """;

        mockMvc.perform(post("/api/v1/threats/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("FRAUD_RISK"))
            .andExpect(jsonPath("$.urlResults[0].status").value("PHISHING"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlsMissing() throws Exception {
        String payload = """
            {
              "message": "test",
              "urls": [],
              "sender": "+51999999999"
            }
            """;

        mockMvc.perform(post("/api/v1/threats/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        AnalyzeThreatUseCase analyzeThreatUseCase() {
            return command -> new ThreatAnalysisResult(
                ThreatAnalysisStatus.FRAUD_RISK,
                List.of(new UrlThreatAnalysis(
                    "http://bbva-seguridad.xyz",
                    UrlThreatStatus.PHISHING,
                    "Detected social engineering (phishing/fraud).",
                    80,
                    List.of(),
                    false,
                    null
                )),
                "GOOGLE_SAFE_BROWSING",
                LocalDateTime.now(),
                1,
                1,
                0
            );
        }
    }
}
