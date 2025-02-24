package com.github.maxonrash.exception;

/**
 * {@link Exception} signaling that specified cityName is not found by API
 */
public class CityWithThisNameIsNotFoundException extends Exception {
    /**
     * Constructs the exception with predefined default message
     */
    public CityWithThisNameIsNotFoundException() {
        super("City with this name is not found");
    }

    /**
     * Constructs the exception with specified message
     * @param cityName a city name that must be included in exception message
     */
    public CityWithThisNameIsNotFoundException(String cityName) {
        super("City with name \"" + cityName + "\" is not found");
    }
}
