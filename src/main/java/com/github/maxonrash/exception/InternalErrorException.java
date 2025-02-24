package com.github.maxonrash.exception;

/**
 * {@link Exception} signaling that an error occurred in API (most likely code 500 messages)
 */
public class InternalErrorException extends Exception {
    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public InternalErrorException(String message) {
        super(message);
    }
}
