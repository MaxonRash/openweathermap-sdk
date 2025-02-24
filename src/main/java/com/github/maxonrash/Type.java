package com.github.maxonrash;

/**
 * Mode type for retrieving current weather.
 * <p>{@link #ON_DEMAND ON_DEMAND} - updates weather data only for specified city if the weather data in storage is outdated
 * <p>{@link #POLLING POLLING} - updates weather data for each city in storage if it is outdated
 */
public enum Type {
    ON_DEMAND,
    POLLING
}
