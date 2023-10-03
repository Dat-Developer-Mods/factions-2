package com.datdeveloper.datfactions.exceptions;

/**
 * An exception for when a faction has already taken the given name
 */
public class FactionNameTakenException extends RuntimeException {
    public FactionNameTakenException() {
    }

    public FactionNameTakenException(final String message) {
        super(message);
    }

    public FactionNameTakenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FactionNameTakenException(final Throwable cause) {
        super(cause);
    }
}
