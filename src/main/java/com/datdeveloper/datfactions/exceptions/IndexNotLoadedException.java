package com.datdeveloper.datfactions.exceptions;

/**
 * Exception thrown when the index is accessed before its ready
 */
public class IndexNotLoadedException extends RuntimeException {
    public IndexNotLoadedException(String message) {
        super(message);
    }
}
