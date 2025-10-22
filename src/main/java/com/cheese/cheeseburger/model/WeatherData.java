package com.cheese.cheeseburger.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="weatherdata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {
    @Id
    private UUID id;
    private LocalDateTime dateTime;
    private String sensor;
    private Double temp;
    private Double windSpeed;
    private Double humidity;
    private Double rain;
}
