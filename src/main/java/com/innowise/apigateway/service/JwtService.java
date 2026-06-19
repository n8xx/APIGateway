package com.innowise.apigateway.service;


import com.innowise.apigateway.model.JwtClaim;
import reactor.core.publisher.Mono;

/**
 * Service responsible for validating JWT tokens and extracting their claims.
 */
public interface JwtService {
    /**
     * Validates the given JWT token and extracts its claims.
     *
     * @param token the JWT token without the "Bearer " prefix
     * @return a {@link Mono} emitting the extracted {@link JwtClaim} on success,
     *         or an error signal if the token is invalid or expired
     */
    Mono<JwtClaim> validateAndExtract(String token);
}