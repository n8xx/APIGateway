package com.innowise.apigateway.service.impl;

import com.innowise.apigateway.config.JwtConfig;
import com.innowise.apigateway.exception.InvalidTokenException;
import com.innowise.apigateway.model.JwtClaim;
import com.innowise.apigateway.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

public class JwtServiceImpl implements JwtService {
    private final JwtConfig jwtConfig;
    private SecretKey secretKey;

    public JwtServiceImpl(JwtConfig jwtConfig){
        this.jwtConfig= jwtConfig;
    }
    @PostConstruct
    private void init(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.secret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<JwtClaim> validateAndExtract(String token){
        return Mono.fromCallable(() -> parseToken(token))
                .onErrorMap(exception -> new InvalidTokenException("Invalid or expired token"));
        }
    private JwtClaim parseToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId =Long.valueOf(claims.getSubject());
        String role = claims.get("role",String.class);

        return new JwtClaim(userId, role);
    }
}

