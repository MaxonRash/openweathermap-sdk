package com.github.maxonrash.store;

import com.github.maxonrash.dto.response.weather.current.CurrentWeatherResponseDTO;
import com.github.maxonrash.entity.CurrentWeatherEntity;
import com.github.maxonrash.exception.CallPerMinuteExceededException;
import com.github.maxonrash.exception.InternalErrorException;
import com.github.maxonrash.exception.InvalidApiKeyException;
import com.github.maxonrash.service.GetCurrentWeatherServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Stores information about cities' weather that have been requested earlier. All requested
 * weather from API stores in internal array <i>data[]</i> and is retrieved from there if
 * it is up-to-date (<10 min delay). Can store only 10 cities at a time, and if new data is
 * added while the storage is full, it replaces the one that was added earlier than the others
 */
@Slf4j
public class StoredCitiesData {
    /**
     * Internal storage of requested cities' weather containing 10 cities at a time
     */
    private static CurrentWeatherEntity[] data = new CurrentWeatherEntity[10];
    /**
     * Ten minutes in seconds to use when checking if the weather is up-to-date
     */
    private static final long TEN_MINUTES_IN_SECONDS = 600L;

    /**
     * Adds weather data to internal storage if it is not present, updates if it is already present
     * or replaces the oldest added one if the storage is already full
     *
     * @param currentWeatherEntity data to be added
     */
    public static void addCurrentWeatherData(CurrentWeatherEntity currentWeatherEntity) {
        int index = indexOfSpecifiedCityInData(currentWeatherEntity.getLat(), currentWeatherEntity.getLon());
        if (index != -1) { // if is already in data[]
            data[index] = currentWeatherEntity;
        } else { // if not in data[] and it is not full
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null) {
                    data[i] = currentWeatherEntity;
                    return;
                }
            }
            for (int i = 1; i < data.length; i++) { // if data[] is already full
                data[i-1] = data[i];
            }
            data[data.length - 1] = currentWeatherEntity;
        }
    }

    /**
     * Returns {@link CurrentWeatherEntity} from storage. Is intended to be used
     * after {@link #isStoredCityWeatherIsUpToDate(double, double) isStoredCityWeatherIsUpToDate} method
     * as it returns empty {@link CurrentWeatherEntity} if city from specified location is not in storage
     *
     * @param lat latitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @param lon longitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @return {@link CurrentWeatherEntity}
     */
    public static CurrentWeatherEntity getCurrentWeatherData(double lat, double lon) {
        int indexOfTheCity = indexOfSpecifiedCityInData(lat, lon);
        if (indexOfTheCity != -1) {
            return data[indexOfTheCity];
        }
        else return new CurrentWeatherEntity();
    }

    /**
     * Updates information for each city in storage. Used when {@link com.github.maxonrash.Type Type} is set to "POLLING" mode
     *
     * @param apiKey API Key for accessing a weather API
     * @throws InvalidApiKeyException if API key is incorrect
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    public static void updateAllCitiesInMemory(String apiKey) throws InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException {
        GetCurrentWeatherServiceImpl service = new GetCurrentWeatherServiceImpl();
        for (int i = 0; i < data.length; i ++) {
            if (data[i] != null) {
                log.info("checking if data for city with lat=" + data[i].getLat() + "&lon=" + data[i].getLon() + " is up-to-date");
                if ( (System.currentTimeMillis() / 1000 - data[i].getDateTime()) > TEN_MINUTES_IN_SECONDS ) {
                    log.info("data is outdated: delay is " + (System.currentTimeMillis() / 1000 - data[i].getDateTime()) + " sec");
                    var newInfo = service.getCurrentWeatherByLatAndLonString( "lat=" + data[i].getLat() + "&lon=" + data[i].getLon(), apiKey);
                    data[i] = CurrentWeatherResponseDTO.convertDTOtoEntity(newInfo);
                } else {
                    log.info("data in storage is up-to-date, no need to update");
                }
            }
        }
    }

    /**
     * Returns true if the difference between current time and time of the city at specified location is less than 10 minutes.
     * Returns false if there is no such city in the storage or the difference is more than 10 minutes
     *
     * @param lat latitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @param lon longitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @return true if the difference between current time and time of the city at specified location is less than 10 minutes.
     * False if there is no such city in the storage or the difference is more than 10 minutes
     */
    public static boolean isStoredCityWeatherIsUpToDate(double lat, double lon) {
        long timeOfStoredCity;
        int storedCityIndex = indexOfSpecifiedCityInData(lat, lon);
        if (storedCityIndex != -1) {
            log.info("Found city with name \"" + data[storedCityIndex].getName() + "\" in storage with index " + storedCityIndex);
            timeOfStoredCity = data[storedCityIndex].getDateTime();
            log.info("Time of " + data[storedCityIndex].getName() + " at index " + storedCityIndex + " is " + timeOfStoredCity);
            long currentTimeMillis = System.currentTimeMillis() / 1000; // to get seconds to match API response
            log.info("Current time in seconds is " + currentTimeMillis);
            log.info("The difference in time is " + (currentTimeMillis - timeOfStoredCity) + " sec");
            return currentTimeMillis - timeOfStoredCity <= TEN_MINUTES_IN_SECONDS; // True if the difference is less than ten minutes
        }
        log.info("City with coordinates lat=" + lat + " & lon=" + lon + " is not found in storage");
        return false;
    }

    /**
     * Returns the index of city with specified location. Returns -1 if there is no such city in data
     * @param lat latitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @param lon longitude (can be got with {@link com.github.maxonrash.service.GetGeocodingService GetGeocodingService}
     * @return int index of city with specified location. Returns -1 if there is no such city in data
     */
    public static int indexOfSpecifiedCityInData(double lat, double lon) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                if ( (Math.abs(data[i].getLat() - lat) <= 0.01) && (Math.abs(data[i].getLon() - lon) <= 0.01) ){
                    return i;
                }
            }
        }
        return -1; // If the city is not found in data stored array
    }
}
