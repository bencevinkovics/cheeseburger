package com.cheese.cheeseburger.service;

import com.cheese.cheeseburger.model.WeatherData;
import com.cheese.cheeseburger.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // optional: use application-test.properties for test DB
@Transactional
class WeatherDataServiceIntegrationTest {

    @Autowired
    private WeatherDataService weatherDataService;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @BeforeEach
    void setUp() {
        // insert some test data
        weatherDataRepository.save(new WeatherData(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 8, 1, 0, 0),
                "s1",
                30.1,
                21.4,
                78.3,
                0.0
        ));
        weatherDataRepository.save(new WeatherData(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 8, 1, 1, 0),
                "s1",
                21.6,
                18.8,
                59.5,
                0.0
        ));
    }

    @Test
    void testGetMetrics_returnsCorrectAverage() {
        List<String> metrics = List.of("temp", "humidity");
        LocalDateTime start = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 1, 23, 59);

        Map<String, Object> result = weatherDataService.getMetrics(
                "avg", List.of("s1"), metrics, start, end
        );

        System.out.println(result);

        double avgTemp = (Double) result.get("temp");
        double roundedAvgTemp = BigDecimal.valueOf(avgTemp).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double avgHum = (Double) result.get("humidity");
        double roundedAvgHum = BigDecimal.valueOf(avgHum).setScale(2, RoundingMode.HALF_UP).doubleValue();


        assertEquals(5, result.size());
        assertEquals("s1", result.get("sensor"));
        assertEquals((30.1 + 21.6) / 2, roundedAvgTemp);
        assertEquals((78.3 + 59.5) / 2, roundedAvgHum);
    }
}
