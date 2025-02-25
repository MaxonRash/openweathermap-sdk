package com.github.maxonrash.dto.response.weather.current;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a part of {@link CurrentWeatherResponseDTO} used for deserializing
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainDTO {
    private double temp;
    @JsonProperty("feels_like")
    private double feelsLike;
    @JsonProperty("temp_min")
    private double tempMin;
    @JsonProperty("temp_max")
    private double tempMax;
    private int pressure;
    private int humidity;
    @JsonProperty("sea_level")
    private int seaLevel;
    @JsonProperty("grnd_level")
    private int grndLevel;
}
