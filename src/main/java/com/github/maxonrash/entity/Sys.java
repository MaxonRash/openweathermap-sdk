package com.github.maxonrash.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This class is a part of {@link CurrentWeatherEntity} used for serializing into JSON
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Sys {
    private long sunrise;
    private long sunset;
}
