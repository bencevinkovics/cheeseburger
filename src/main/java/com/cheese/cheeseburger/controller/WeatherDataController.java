package com.cheese.cheeseburger.controller;

import com.cheese.cheeseburger.model.WeatherData;
import com.cheese.cheeseburger.service.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class WeatherDataController {

    private final WeatherDataService weatherDataService;

    @Autowired
    public WeatherDataController(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    @GetMapping("/weatherData/all")
    public List<WeatherData> getAllWeatherData() {
        return weatherDataService.findAll();
    }

    @GetMapping("weatherData")
    public Map<String, Object> getMetrics(
            @RequestParam(required = false) String statistic,
            @RequestParam(required = false) List<String> sensors,
            @RequestParam(required = false) List<String> metrics,
            @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)LocalDateTime end
    ) {
        return weatherDataService.getMetrics(statistic, sensors, metrics, start, end);
    }
}
