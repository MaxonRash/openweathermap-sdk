package com.github.maxonrash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * This class is used in {@link com.github.maxonrash.CurrentWeatherSDK CurrentWeatherSDK} to deserialize received response from API
 * and as stored data in {@link com.github.maxonrash.store.StoredCitiesData StoredCitiesData}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CurrentWeatherEntity {
    @JsonIgnore
    private double lon;
    @JsonIgnore
    private double lat;
    private Weather weather;
    private Temperature temperature;
    private int visibility;
    private Wind wind;
    @JsonProperty("datetime")
    private long dateTime;
    private Sys sys;
    private long timezone;
    private String name;
}
