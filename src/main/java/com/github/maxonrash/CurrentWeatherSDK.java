package com.github.maxonrash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maxonrash.dto.response.geocoding.GetGeocodingResponseDTO;
import com.github.maxonrash.dto.response.weather.current.CurrentWeatherResponseDTO;
import com.github.maxonrash.entity.CurrentWeatherEntity;
import com.github.maxonrash.exception.*;
import com.github.maxonrash.service.GetCurrentWeatherService;
import com.github.maxonrash.service.GetGeocodingService;
import com.github.maxonrash.store.StoredCitiesData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that is used to access Weather API
 */
@Slf4j
@Getter
public class CurrentWeatherSDK {
    /**
     * internal storage of added API keys. {@link #create(String, Type, GetGeocodingService, GetCurrentWeatherService) Creating}
     * will add API key to this storage if it is not present, or return instance of {@link CurrentWeatherSDK} if it is already present
     */
    private static List<CurrentWeatherSDK> currentWeatherSDKList;
    /**
     * API Key to access weather API
     */
    private String apiKey;
    /**
     * Mode type for retrieving current weather.
     * <p>{@link Type#ON_DEMAND ON_DEMAND} - updates weather data only for specified city if the weather data in storage is outdated
     * <p>{@link Type#POLLING POLLING} - updates weather data for each city in storage if it is outdated
     */
    private Type currentModeType;
    /**
     * Geocoding service that will be used
     */
    private final GetGeocodingService getGeocodingService;
    /**
     * Service for retrieving current weather data that will be used
     */
    private final GetCurrentWeatherService getCurrentWeatherService;

    /**
     * Constructs the object with specified parameters. Cannot be used from outside this class.
     * Used by {@link #create(String, Type, GetGeocodingService, GetCurrentWeatherService) create} method
     * that must be used for getting instances of this object
     *
     * @param apiKey API Key for accessing a weather API
     * @param modeType Mode type for retrieving current weather.
     * @param getGeocodingService implementation of {@link GetGeocodingService} for getting coordinates of a city
     * @param getCurrentWeatherService implementation of {@link GetCurrentWeatherService} for getting current weather for a location
     */
    private CurrentWeatherSDK(String apiKey, Type modeType, GetGeocodingService getGeocodingService, GetCurrentWeatherService getCurrentWeatherService) {
        this.getCurrentWeatherService = getCurrentWeatherService;
        this.getGeocodingService = getGeocodingService;
        this.apiKey = apiKey;
        this.currentModeType = modeType;
    }

    /**
     * Main way of creating an instance of the object. It will add API key to internal {@link #currentWeatherSDKList} storage
     * if it is not present, or return instance of {@link CurrentWeatherSDK} if it is already present
     *
     * @param  apiKey API Key for accessing a weather API. Cannot be null, blank and length must be up to 50 chars
     * @param modeType Mode type for retrieving current weather.
     * @param getGeocodingService implementation of {@link GetGeocodingService} for getting coordinates of a city
     * @param getCurrentWeatherService implementation of {@link GetCurrentWeatherService} for getting current weather for a location
     * @return instance of {@link CurrentWeatherSDK}
     */
    public static CurrentWeatherSDK create(String apiKey, Type modeType, GetGeocodingService getGeocodingService, GetCurrentWeatherService getCurrentWeatherService) {
        if (apiKey == null || apiKey.isBlank() || apiKey.length() > 50) {
            throw new ApiKeyIsNullOrEmptyException();
        }
        if (currentWeatherSDKList == null) {
            currentWeatherSDKList = new ArrayList<>();
        }
        for (CurrentWeatherSDK obj : currentWeatherSDKList) {
            if (obj.apiKey.equals(apiKey)) {
                obj.setCurrentModeType(modeType);
                return obj;
            }
        }
        CurrentWeatherSDK currentWeatherSDK = new CurrentWeatherSDK(apiKey, modeType, getGeocodingService, getCurrentWeatherService);
        CurrentWeatherSDK.currentWeatherSDKList.add(currentWeatherSDK);
        return currentWeatherSDK;
    }

    /**
     * Removes specified API Key from internal {@link #currentWeatherSDKList} storage
     *
     * @param apiKey API Key for accessing a weather API
     */
    public static void delete(String apiKey) {
        if (currentWeatherSDKList != null) {
            currentWeatherSDKList.removeIf(obj -> obj.getApiKey().equals(apiKey));
            log.info("removed apiKey \"" + apiKey.substring(0,15) + "...\" from memory");
        }
        else {
            log.info("no such key in memory");
        }
    }

    /**
     * Removes all API Keys from internal {@link #currentWeatherSDKList} storage
     */
    public static void deleteAllKeys() {
        log.info("removed all apiKeys from memory");
        currentWeatherSDKList = null;
    }

    /**
     * Returns true if specified API key is in internal {@link #currentWeatherSDKList} storage.
     *
     * @param apiKey API Key for accessing a weather API
     * @return true if API key is in the storage, false if not or the storage is null
     */
    public static boolean isObjectWithThisApiKeyAlreadyExists(String apiKey) {
        if (currentWeatherSDKList != null) {
            var optional = currentWeatherSDKList.stream().filter(obj -> obj.getApiKey().equals(apiKey)).findFirst();
            log.info("presence of apiKey \"" + apiKey.substring(0,15) + "...\" is " + optional.isPresent());
            return optional.isPresent();
        }
        log.info("no keys in store yet");
        return false;
    }

    /**
     * Returns JSON string of {@link CurrentWeatherEntity} <p>While {@link Type} mode is {@link Type#ON_DEMAND} gets the
     * weather data from {@link StoredCitiesData} if it is up-to-date (within 10 minutes). If not - updates data only for
     * specified city before retrieving.<p> While {@link Type} mode is {@link Type#POLLING} updates data of each city stored
     * in {@link StoredCitiesData} if it is not up-to-date (within 10 minutes) before retrieving for each request.
     *
     * @param cityName name of city that must follow pattern "^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$" Example: Rostov_na_donu
     * @return JSON string of {@link CurrentWeatherEntity}
     * @throws JsonProcessingException if response from weather API is wrong (cannot happen until API response is changed)
     * @throws InvalidApiKeyException if API key is incorrect or haven't been activated yet (may take a while)
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     * @throws CityWithThisNameIsNotFoundException if the city with specified name is not found by weather API
     */
    public String retrieveCurrentWeatherJSON(String cityName) throws JsonProcessingException, InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException, CityWithThisNameIsNotFoundException {
        CurrentWeatherEntity currentWeatherEntity;
        var geo = getGeocodingInfo(cityName);
        double lat = geo.getLat();
        double lon = geo.getLon();

        if (currentModeType == Type.POLLING) {
            log.info("Trying to update each city in storage for POLLING mode");
            StoredCitiesData.updateAllCitiesInMemory(this.apiKey);
        }
        log.info("Checking if data for city \"" + cityName + "\" exists in storage and is up-to-date");
        if (StoredCitiesData.isStoredCityWeatherIsUpToDate(lat, lon)) {
            currentWeatherEntity = StoredCitiesData.getCurrentWeatherData(lat, lon);
            log.info("Took info for city \"" + cityName + "\" from storage because it is up-to-date");
        } else {
            var currentWeatherResponseDTO = getCurrentWeatherService.getCurrentWeatherByLatAndLonString("lat=" + lat + "&lon=" + lon, this.apiKey);
            currentWeatherEntity = CurrentWeatherResponseDTO.convertDTOtoEntity(currentWeatherResponseDTO);
            StoredCitiesData.addCurrentWeatherData(currentWeatherEntity);
        }
        log.info("Final JSON string to return: ");
        var jsonString = new ObjectMapper().writeValueAsString(currentWeatherEntity);
        log.info(jsonString);
        log.info("_________________________________________________________________________");
        return jsonString;
    }

    /**
     * Returns {@link GetGeocodingResponseDTO} object containing data for specified city
     *
     * @param cityName name of city that must follow pattern "^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$" Example: Rostov_na_donu
     * @return {@link GetGeocodingResponseDTO} object containing data for specified city
     * @throws CityWithThisNameIsNotFoundException if the city with specified name is not found by weather API
     * @throws InvalidApiKeyException if API key is incorrect or haven't been activated yet (may take a while)
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    public GetGeocodingResponseDTO getGeocodingInfo(String cityName) throws CityWithThisNameIsNotFoundException, InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException {
        var geocodingInfo = getGeocodingService.getGeocodingByCityName(cityName, this.apiKey);
        return geocodingInfo[0];
    }

    /**
     * Returns JSON string of {@link GetGeocodingResponseDTO} object containing data for specified city
     *
     * @param cityName name of city that must follow pattern "^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$" Example: Rostov_na_donu
     * @return JSON string of {@link GetGeocodingResponseDTO} object containing data for specified city
     * @throws CityWithThisNameIsNotFoundException if the city with specified name is not found by weather API
     * @throws InvalidApiKeyException if API key is incorrect or haven't been activated yet (may take a while)
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    public String getGeocodingInfoJSON(String cityName) throws CityWithThisNameIsNotFoundException, InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException {
        var geocodingInfo = getGeocodingService.getGeocodingByCityName(cityName, this.apiKey);
        try {
            return new ObjectMapper().writeValueAsString(geocodingInfo[0]);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the {@link Type} of current object
     * @param currentModeType {@link Type#ON_DEMAND ON_DEMAND} or {@link Type#POLLING POLLING}
     */
    public void setCurrentModeType(Type currentModeType) {
        this.currentModeType = currentModeType;
        log.info("changed mode type for apiKey \"" + apiKey.substring(0,15) + "...\" to " + currentModeType.name());
    }

}