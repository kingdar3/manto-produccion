package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.BlacklistUrl;
import com.guardianapp.domain.port.in.BlacklistUrlUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlacklistUrlController.class)
@Import(BlacklistUrlControllerTest.StubConfig.class)
class BlacklistUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterBlacklistUrl() throws Exception {
        String payload = """
            {
              "url": "http://sitio-malicioso.com"
            }
            """;

        mockMvc.perform(post("/api/v1/blacklist/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.url").value("http://sitio-malicioso.com"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlMissing() throws Exception {
        String payload = """
            {
              "url": ""
            }
            """;

        mockMvc.perform(post("/api/v1/blacklist/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        BlacklistUrlUseCase blacklistUrlUseCase() {
            return command -> new BlacklistUrl(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                command.url().trim(),
                LocalDateTime.now()
            );
        }
    }
}
