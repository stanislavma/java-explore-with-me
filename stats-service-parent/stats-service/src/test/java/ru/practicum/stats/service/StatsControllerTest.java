package ru.practicum.stats.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.EndpointHitDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EndpointHitDto endpointHitDto;

    @BeforeEach
    void setUp() {
        endpointHitDto = new EndpointHitDto(
                1L,
                "ewm-main-service",
                "/events/1",
                "192.163.0.1",
                "2024-09-06 11:00:23"
        );
    }

    @Test
    void saveStats_shouldReturnOk_whenStatsAreValid() throws Exception {
        mockMvc.perform(post("/stats/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getStats_shouldReturnStats_whenStatsExist() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2024-09-06 00:00:00")
                        .param("end", "2024-09-07 00:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].app").value("ewm-main-service"))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(1));
    }
}
