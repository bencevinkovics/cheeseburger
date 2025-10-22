package com.cheese.cheeseburger.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class WeatherDataServiceTest {

    @InjectMocks
    @Spy
    private WeatherDataService weatherDataService;

    @Test
    void buildSqlTest() {
        List<String> sensors = Arrays.asList("s1", "s2");
        List<String> metrics = Arrays.asList("temp", "rain");
        LocalDateTime start = LocalDateTime.parse("2025-08-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2025-08-01T23:59:59");

        String res = weatherDataService.buildSql(sensors,metrics,start,end);
        String expectedSql = "SELECT sensor, temp, rain FROM weatherdata WHERE 1=1 AND sensor IN ('s1','s2') AND date_time >= :start AND date_time <= :end";

        System.out.println(res);
        Assertions.assertEquals(expectedSql, res);
    }

    @Test
    void buildSqlTestNoSensorProvided() {
        List<String> sensors = null;
        List<String> metrics = Arrays.asList("temp", "rain");
        LocalDateTime start = LocalDateTime.parse("2025-08-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2025-08-01T23:59:59");

        String res = weatherDataService.buildSql(sensors,metrics,start,end);
        String expectedSql = "SELECT sensor, temp, rain FROM weatherdata WHERE 1=1 AND date_time >= :start AND date_time <= :end";

        System.out.println(res);
        Assertions.assertEquals(expectedSql, res);
    }

    @Test
    void organizeResultsTest() {

        List<Object[]> resultList = List.of(
                new Object[] {"s1", 30.1, 21.4, 78.3, 0},
                new Object[] {"s1", 21.6, 18.8, 59.5, 0},
                new Object[] {"s1", 24.1, 10.5, 89.0, 0}
        );
        List<String> metrics = List.of("temp", "windSpeed", "humidity", "rain");

        List<Map<String, Object>> res = weatherDataService.organizeResults(resultList, metrics);
        List<Map<String, Object>> expected =  List.of(
                new LinkedHashMap<>() {{
                    put("sensor", "s1");
                    put("temp", 30.1);
                    put("windSpeed", 21.4);
                    put("humidity", 78.3);
                    put("rain", 0);
                }},
                new LinkedHashMap<>() {{
                    put("sensor", "s1");
                    put("temp", 21.6);
                    put("windSpeed", 18.8);
                    put("humidity", 59.5);
                    put("rain", 0);
                }},
                new LinkedHashMap<>() {{
                    put("sensor", "s1");
                    put("temp", 24.1);
                    put("windSpeed", 10.5);
                    put("humidity", 89.0);
                    put("rain", 0);
                }}
        );

        Assertions.assertEquals(expected,res);
    }

    @Test
    void testGetMetrics() {

        String statistic = "avg";
        List<String> sensors = List.of("s1","s2");
        List<String> metrics = List.of("temp", "rain");
        LocalDateTime start = LocalDateTime.parse("2025-08-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2025-08-01T23:59:59");

        Map<String, Object> aggResults =  new LinkedHashMap<>();
        aggResults.put("temp", 22.93);
        aggResults.put("rain", 0.1);

        Mockito.doReturn("Mocked query").when(weatherDataService).buildSql(anyList(), anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
        Mockito.doReturn(new ArrayList<>()).when(weatherDataService).executeQuery(Mockito.anyString(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
        Mockito.doReturn(new ArrayList<>()).when(weatherDataService).organizeResults(anyList(), anyList());
        Mockito.doReturn(aggResults).when(weatherDataService).aggregateResults(anyList(), anyList(), Mockito.anyString());

        Map<String, Object> res = weatherDataService.getMetrics(statistic, sensors, metrics, start, end);

        Map<String, Object> expected =  new LinkedHashMap<>();
        expected.put("temp", 22.93);
        expected.put("rain", 0.1);
        expected.put("startDate", start);
        expected.put("endDate", end);
        expected.put("sensor","s1,s2");

        Assertions.assertEquals(expected,res);

        Mockito.verify(weatherDataService).buildSql(eq(sensors), eq(metrics), eq(start), eq(end));
        Mockito.verify(weatherDataService).aggregateResults(anyList(), eq(metrics), eq(statistic));
    }

}
