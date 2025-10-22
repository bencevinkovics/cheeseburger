package com.cheese.cheeseburger.repository;

import com.cheese.cheeseburger.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {

}
