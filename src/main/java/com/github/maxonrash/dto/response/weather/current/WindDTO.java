package com.github.maxonrash.dto.response.weather.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a part of {@link CurrentWeatherResponseDTO} used for deserializing
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindDTO {
    private double speed;
    private int deg;
    private double gust;
}
