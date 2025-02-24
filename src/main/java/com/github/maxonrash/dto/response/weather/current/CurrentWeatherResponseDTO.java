package com.github.maxonrash.dto.response.weather.current;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.maxonrash.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used in {@link com.github.maxonrash.service.GetCurrentWeatherServiceImpl GetCurrentWeatherServiceImpl}
 * to deserialize received JSON response from API
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentWeatherResponseDTO {
    private CoordDTO coord;
    @JsonProperty("weather")
    private WeatherDTO[] weather;
    private String base;
    @JsonProperty("main")
    private MainDTO main;
    private int visibility;
    @JsonProperty("wind")
    private WindDTO wind;
    @JsonProperty("rain")
    private RainDTO rain;
    @JsonProperty("snow")
    private SnowDTO snow;
    @JsonProperty("clouds")
    private CloudsDTO clouds;
    private long dt;
    @JsonProperty("sys")
    private SysDTO sys;
    private int timezone;
    private long id;
    private String name;
    private int cod;

    /**
     * Converts {@link CurrentWeatherResponseDTO} into a new instance of {@link CurrentWeatherEntity}
     *
     * @param dto {@link CurrentWeatherResponseDTO} object
     * @return an instance of {@link CurrentWeatherEntity}
     */
    public static CurrentWeatherEntity convertDTOtoEntity(CurrentWeatherResponseDTO dto) {
        return CurrentWeatherEntity.builder()
                .lat(dto.getCoord().getLat())
                .lon(dto.getCoord().getLon())
                .weather(new Weather(dto.getWeather()[0].getMain(), dto.getWeather()[0].getDescription()))
                .temperature(new Temperature(dto.getMain().getTemp(), dto.getMain().getFeelsLike()))
                .visibility(dto.getVisibility())
                .wind(new Wind(dto.getWind().getSpeed()))
                .dateTime(dto.getDt())
                .sys(new Sys(dto.getSys().getSunrise(), dto.getSys().getSunset()))
                .timezone(dto.getTimezone())
                .name(dto.getName())
                .build();
    }
}
