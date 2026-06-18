package com.innowise.apigateway.model;

import java.time.Instant;
/**
 * Represents a structured error response returned to the client.
 *
 * @param timestamp the moment the error occurred
 * @param status    the HTTP status code
 * @param message   a human-readable description of the error
 */
public record ErrorResponse(Instant timestamp, int status, String message) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(Instant.now(), status, message);
    }
}