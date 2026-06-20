package com.innowise.apigateway.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for JWT token validation.
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtConfig(

        @NotBlank
        String secret,

        @NotEmpty
        List<String> whitelist
) {
}