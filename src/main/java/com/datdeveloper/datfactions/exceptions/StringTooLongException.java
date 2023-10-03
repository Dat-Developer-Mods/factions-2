package com.datdeveloper.datfactions.exceptions;

/**
 * An exception that is thrown when a String exceeds its maximum length
 */
public class StringTooLongException extends RuntimeException {
    /** The maximum length that was exceeded */
    final int maxLength;
    final String string;

    public StringTooLongException(final int maxLength, final String string) {
        super();
        this.maxLength = maxLength;
        this.string = string;
    }

    public StringTooLongException(final int maxLength, final String string, final String message) {
        super(message);
        this.maxLength = maxLength;
        this.string = string;
    }

    public StringTooLongException(final int maxLength, final String string, final String message, final Throwable cause) {
        super(message, cause);
        this.maxLength = maxLength;
        this.string = string;
    }

    public StringTooLongException(final int maxLength, final String string, final Throwable cause) {
        super(cause);
        this.maxLength = maxLength;
        this.string = string;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getString() {
        return string;
    }
}
