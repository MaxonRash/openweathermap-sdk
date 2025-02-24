package com.github.maxonrash.service;

import com.github.maxonrash.dto.response.geocoding.GetGeocodingResponseDTO;
import com.github.maxonrash.exception.CallPerMinuteExceededException;
import com.github.maxonrash.exception.CityWithThisNameIsNotFoundException;
import com.github.maxonrash.exception.InternalErrorException;
import com.github.maxonrash.exception.InvalidApiKeyException;

/**
 * Interface for accessing weather API geocoding service. The default implementation is {@link GetGeocodingServiceImpl}
 * Can be implemented by another class to use another weather API geocoding service
 */
public interface GetGeocodingService {
    /**
     * Default implementation of {@link GetGeocodingService}
     */
    GetGeocodingService DEFAULT_SERVICE = new GetGeocodingServiceImpl();

    /**
     * Returns an array of found cities {@link GetGeocodingResponseDTO} for specified cityName
     *
     * @param cityName name of city that must follow pattern "^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$" Example: Rostov_na_donu
     * @param apiKey API Key for accessing a weather API
     * @return array of {@link GetGeocodingResponseDTO}
     * @throws InvalidApiKeyException if API key is incorrect
     * @throws CityWithThisNameIsNotFoundException if the city with specified name is not found by weather API
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    GetGeocodingResponseDTO[] getGeocodingByCityName(String cityName, String apiKey) throws InvalidApiKeyException, CityWithThisNameIsNotFoundException, CallPerMinuteExceededException, InternalErrorException;
}
