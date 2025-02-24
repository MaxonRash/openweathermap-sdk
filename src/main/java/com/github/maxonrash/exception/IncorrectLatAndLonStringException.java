package com.github.maxonrash.exception;

/**
 * {@link RuntimeException} signaling that specified LatAndLonString doesn't follow the pattern
 */
public class IncorrectLatAndLonStringException extends RuntimeException {
    /**
     * Constructs the exception with predefined default message
     */
    public IncorrectLatAndLonStringException() {
        super("LatAndLon parameter must match pattern \"^lat=-?\\d{1,2}\\.\\d{2,8}&lon=-?\\d{1,3}\\.\\d{2,8}$\" \r\n Example: \"lat=55.7522&lon=37.6156\"");
    }

    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public IncorrectLatAndLonStringException(String message) {
        super(message);
    }
}
