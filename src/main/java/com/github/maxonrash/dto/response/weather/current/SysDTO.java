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
public class SysDTO {
    private int type;
    private long id;
    private String country;
    private long sunrise;
    private long sunset;
}
