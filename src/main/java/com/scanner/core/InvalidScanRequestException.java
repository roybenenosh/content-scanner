package com.scanner.core;

/** Thrown when a scan request violates an input rule; the caller's fault, not recoverable by retrying. */
public class InvalidScanRequestException extends RuntimeException {
    public InvalidScanRequestException(String message) {
        super(message);
    }
}
