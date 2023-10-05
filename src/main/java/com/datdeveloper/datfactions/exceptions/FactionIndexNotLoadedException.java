package com.datdeveloper.datfactions.exceptions;

/**
 * Exception thrown when the index is accessed before its ready
 */
public class FactionIndexNotLoadedException extends RuntimeException {
    public FactionIndexNotLoadedException() {
        super("Index accessed before it's ready");
    }
}
