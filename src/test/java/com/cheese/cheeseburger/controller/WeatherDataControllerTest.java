package com.cheese.cheeseburger.controller;

import com.cheese.cheeseburger.service.WeatherDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherDataController.class)
class WeatherDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherDataService weatherDataService;

    @Test
    void testGetMetricsEndpoint() throws Exception {
        // Arrange: mock the service response
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("sensor", "s1");
        mockResult.put("temp", 30.1);

        // Use more specific argument matchers for clarity and type-safety
        Mockito.when(weatherDataService.getMetrics(
                anyString(),
                any(),
                any(),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(mockResult); // The cast is not necessary

        // Act & Assert: perform GET request and validate response
        mockMvc.perform(get("/weatherData")
                        .param("statistic", "avg")
                        .param("sensor", "s1")
                        .param("metrics", "temp")
                        // Add required date params to make the test more realistic
                        .param("start", "2025-08-01T00:00:00")
                        .param("end", "2025-08-01T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensor").value("s1"))
                .andExpect(jsonPath("$.temp").value(30.1));
    }
}
