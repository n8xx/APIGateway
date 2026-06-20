package com.innowise.apigateway.dto.iternal;
/**
 * Represents extracted claims from a validated JWT token.
 *
 * @param userId the ID of the authenticated user
 * @param role   the role of the authenticated user
 */
public record JwtClaim(Long userId, String role) {
}