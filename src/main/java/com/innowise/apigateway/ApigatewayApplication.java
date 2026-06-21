package com.innowise.apigateway;

import com.innowise.apigateway.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {
        ReactiveSecurityAutoConfiguration.class,
        ReactiveUserDetailsServiceAutoConfiguration.class
})
@EnableConfigurationProperties(JwtConfig.class)
public class ApigatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }

}
