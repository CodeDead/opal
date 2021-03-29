package com.codedead.opal.domain;

public final class InvalidHttpResponseCodeException extends Exception {

    /**
     * Initialize a new InvalidHttpResponseCodeException
     */
    public InvalidHttpResponseCodeException() {
        super();
    }

    /**
     * Initialize a new InvalidHttpResponseCodeException
     *
     * @param message The error message
     */
    public InvalidHttpResponseCodeException(final String message) {
        super(message);
    }
}
