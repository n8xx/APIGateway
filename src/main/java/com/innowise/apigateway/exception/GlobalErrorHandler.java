package com.innowise.apigateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.apigateway.model.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;


@Component
@Order(-1)
public class GlobalErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable throwable) {
        if (!(throwable instanceof InvalidTokenException || throwable instanceof MissingTokenException)) {
            return Mono.error(throwable);
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                throwable.getMessage()
        );

        return writeResponse(response, errorResponse);
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, ErrorResponse errorResponse) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorResponse))
                .flatMap(bytes -> {
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return response.writeWith(Mono.just(buffer));
                });
    }
}