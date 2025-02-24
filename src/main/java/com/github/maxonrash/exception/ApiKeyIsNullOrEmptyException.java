package com.github.maxonrash.exception;

/**
 * {@link RuntimeException} signaling that specified API Key in empty or null
 */
public class ApiKeyIsNullOrEmptyException extends RuntimeException {
    /**
     * Constructs the exception with predefined default message
     */
    public ApiKeyIsNullOrEmptyException() {
        super("API key cannot be null or empty");
    }

    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public ApiKeyIsNullOrEmptyException(String message) {
        super(message);
    }
}
