package com.datdeveloper.datfactions.exceptions;

/**
 * An exception that is thrown when a String exceeds its maximum length
 */
public class StringTooLongException extends RuntimeException {
    /** The maximum length that was exceeded */
    final int maxLength;
    /** The string that failed the length check */
    final String string;

    /**
     * @param maxLength The exceeded maximum length of the string
     * @param string The string that's too long
     */
    public StringTooLongException(final int maxLength, final String string) {
        super();
        this.maxLength = maxLength;
        this.string = string;
    }

    /**
     * @param maxLength The exceeded maximum length of the string
     * @param string The string that's too long
     * @param message The detail message
     */
    public StringTooLongException(final int maxLength, final String string, final String message) {
        super(message);
        this.maxLength = maxLength;
        this.string = string;
    }

    /**
     * @param maxLength The exceeded maximum length of the string
     * @param string The string that's too long
     * @param message The detail message
     * @param cause The cause
     */
    public StringTooLongException(final int maxLength, final String string, final String message, final Throwable cause) {
        super(message, cause);
        this.maxLength = maxLength;
        this.string = string;
    }

    /**
     * @param maxLength The exceeded maximum length of the string
     * @param string The string that's too long
     * @param cause The cause
     */
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