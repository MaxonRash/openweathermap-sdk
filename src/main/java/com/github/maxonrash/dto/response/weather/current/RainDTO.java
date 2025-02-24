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
public class RainDTO {
    @JsonProperty("1h")
    private double oneH;
}
