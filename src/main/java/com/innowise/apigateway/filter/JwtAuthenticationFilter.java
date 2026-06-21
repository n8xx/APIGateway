package com.innowise.apigateway.filter;

import com.innowise.apigateway.config.JwtConfig;
import com.innowise.apigateway.exception.MissingTokenException;
import com.innowise.apigateway.dto.iternal.JwtClaim;
import com.innowise.apigateway.service.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String USER_ID_HEADER = "X-User-Id";
  private static final String USER_ROLE_HEADER = "X-User-Role";

  private final JwtService jwtService;
  private final JwtConfig jwtConfig;

  private final AntPathMatcher matcher = new AntPathMatcher();

  public JwtAuthenticationFilter(JwtService jwtService, JwtConfig jwtConfig) {
    this.jwtService = jwtService;
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if (isWhitelisted(path)) {
      return chain.filter(exchange);
    }

    return Mono.defer(
        () -> {
          String token;
          try {
            token = extractToken(exchange.getRequest());
          } catch (MissingTokenException e) {
            return Mono.error(e);
          }
          return jwtService
              .validateAndExtract(token)
              .flatMap(claim -> chain.filter(mutateExchange(exchange, claim)));
        });
  }

  @Override
  public int getOrder() {
    return -100;
  }

  private boolean isWhitelisted(String path) {
      return jwtConfig.whitelist().stream()
              .anyMatch(pattern -> matcher.match(pattern, path));
  }

  private String extractToken(ServerHttpRequest request) {
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      throw new MissingTokenException("Authorization header is missing or malformed");
    }

    return authHeader.substring(BEARER_PREFIX.length());
  }

  private ServerWebExchange mutateExchange(ServerWebExchange exchange, JwtClaim claim) {
    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header(USER_ID_HEADER, String.valueOf(claim.userId()))
            .header(USER_ROLE_HEADER, claim.role())
            .build();

    return exchange.mutate().request(mutatedRequest).build();
  }
}