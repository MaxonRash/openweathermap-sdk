package com.github.maxonrash.service;

import com.github.maxonrash.dto.response.weather.current.CurrentWeatherResponseDTO;
import com.github.maxonrash.exception.CallPerMinuteExceededException;
import com.github.maxonrash.exception.InternalErrorException;
import com.github.maxonrash.exception.InvalidApiKeyException;

/**
 * Interface for accessing weather API. The default implementation is {@link GetCurrentWeatherServiceImpl}
 * Can be implemented by another class to use another weather API
 */
public interface GetCurrentWeatherService {
    /**
     * Default implementation of {@link GetCurrentWeatherService}
     */
    GetCurrentWeatherService DEFAULT_SERVICE = new GetCurrentWeatherServiceImpl();

    /**
     * Returns deserialized JSON response {@link CurrentWeatherResponseDTO} for specified <i>latitude</i> and <i>longitude</i>
     *
     * @param latAndLon string representation of <i>latitude</i> and <i>longitude</i>. Must match the pattern
     * "^lat=-?\d{1,2}\.\d{2,8}&lon=-?\d{1,3}\.\d{2,8}$" Example: "lat=55.7522&lon=37.6156"
     * @param apiKey API Key for accessing a weather API
     * @return instance of {@link CurrentWeatherResponseDTO}
     * @throws InvalidApiKeyException if API key is incorrect
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    CurrentWeatherResponseDTO getCurrentWeatherByLatAndLonString(String latAndLon, String apiKey) throws InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException;
}
