package com.cheese.cheeseburger.service;

import com.cheese.cheeseburger.model.WeatherData;
import com.cheese.cheeseburger.repository.WeatherDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherDataService {

    @Autowired
    private EntityManager entityManager;

    WeatherDataRepository weatherDataRepository;

    @Autowired
    public WeatherDataService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    public List<WeatherData> findAll() {
        return weatherDataRepository.findAll();
    }

    public Map<String, Object> getMetrics(String statistic, List<String> sensors, List<String> metrics, LocalDateTime start, LocalDateTime end) {

        if (metrics == null || metrics.isEmpty()) {
            throw new IllegalArgumentException("Metrics parameter(s) should always be provided.");
        }
        if((start == null && end != null)||(start != null && end == null)) {
            throw new IllegalArgumentException("Both sides of the interval should be provided");
        }

        String finalStatistic = (statistic == null || statistic.isBlank()) ? "avg" : statistic;

        String sql = buildSql(sensors, metrics,  start, end);

        List<Object[]> resultList = executeQuery(sql, start, end);

        List<Map<String, Object>> results = organizeResults(resultList, metrics);

        Map<String, Object> aggResults = aggregateResults(results, metrics, finalStatistic);

        String sensorList = (sensors != null && !sensors.isEmpty()) ? String.join(",", sensors) : "all";

        aggResults.put("startDate", start);
        aggResults.put("endDate", end);
        aggResults.put("sensor", sensorList);

        return aggResults;
    }

    public String buildSql(List<String> sensors, List<String> metrics, LocalDateTime start, LocalDateTime end) {
        String metricsCols = metrics.stream()
                .map("%s"::formatted)
                .collect(Collectors.joining(", "));

        StringBuilder sql = new StringBuilder("SELECT sensor, " + metricsCols + " FROM weatherdata WHERE 1=1");

        if (sensors != null && !sensors.isEmpty()) {
            String inClause = sensors.stream()
                    .map(s -> "'" + s + "'")
                    .collect(Collectors.joining(","));
            sql.append(" AND sensor IN (").append(inClause).append(")");
        }

        if(start != null) sql.append(" AND date_time >= :start");
        if(end != null) sql.append(" AND date_time <= :end");

        return sql.toString();
    }

    public List<Object[]> executeQuery(String sql, LocalDateTime start, LocalDateTime end) {
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("start", Timestamp.valueOf(start));
        query.setParameter("end", Timestamp.valueOf(end));
        return query.getResultList();
    }

    public List<Map<String, Object>> organizeResults(List<Object[]> resultList, List<String> metrics) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("sensor", row[0]);
            for(int i = 0; i < metrics.size(); i++) {
                map.put("%s".formatted(metrics.get(i)), row[i +1]);
            }
            results.add(map);
        }
        return results;
    }

    public Map<String, Object> aggregateResults(List<Map<String, Object>> results, List<String> metrics, String finalStatistic) {
        Map<String, Object> aggResults = new LinkedHashMap<>();

        for (String metric : metrics) {
            DoubleSummaryStatistics stats = results.stream()
                    .map(map -> map.get(metric))
                    .filter(Objects::nonNull)
                    .mapToDouble(val -> ((Number) val).doubleValue())
                    .summaryStatistics();

            double aggValue;
            switch (finalStatistic.toLowerCase()) {
                case "min":
                    aggValue = stats.getMin();
                    break;
                case "max":
                    aggValue = stats.getMax();
                    break;
                default: // avg
                    aggValue = stats.getAverage();
                    break;
            }

            aggResults.put(metric, aggValue);
        }

        return aggResults;
    }

}
