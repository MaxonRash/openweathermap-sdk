package com.github.maxonrash.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Temperature {
    private double temp;
    @JsonProperty("feels_like")
    private double feelsLike;
}
