package com.github.maxonrash.exception;

/**
 * {@link Exception} signaling that the limit for your account/ApiKey is exceeded
 */
public class CallPerMinuteExceededException extends Exception {
    /**
     * Constructs the exception with predefined default message
     */
    public CallPerMinuteExceededException() {
        super("You've exceeded the limit of call for your account. Please try again later or upgrade your plan");
    }

    /**
     * Constructs the exception with specified message
     * @param message the detail message
     */
    public CallPerMinuteExceededException(String message) {
        super(message);
    }
}
