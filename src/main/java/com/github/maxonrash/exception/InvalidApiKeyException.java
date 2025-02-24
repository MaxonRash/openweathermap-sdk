package com.github.maxonrash.exception;

/**
 * {@link Exception} signaling that specified API key is wrong or haven't been activated yet (may take some time)
 */
public class InvalidApiKeyException extends Exception {
    /**
     * Constructs the exception with predefined default message
     */
    public InvalidApiKeyException() {
        super("API key is invalid. It is either wrong or haven't been activated yet");
    }

    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public InvalidApiKeyException(String message) {
        super(message);
    }

}
