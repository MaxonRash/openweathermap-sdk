package com.github.maxonrash.exception;

/**
 * {@link RuntimeException} signaling that specified cityName doesn't follow the pattern
 */
public class IncorrectCityNameException extends RuntimeException {
    /**
     * Constructs the exception with predefined default message
     */
    public IncorrectCityNameException() {
        super("cityName must not be null and follow pattern \"^w[w-_]{0,30}$\" Example : Rostov_na_donu");
    }

    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public IncorrectCityNameException(String message) {
        super(message);
    }
}
