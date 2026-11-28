package com.upc.webmarketar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    CorsConfigurationSource cors(@Value("${marketar.cors.origins}") String origins) {
        var c = new CorsConfiguration();
        c.setAllowedOrigins(Arrays.asList(origins.split(",")));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Idempotency-Key", "X-Cart-Id", "X-Request-ID"));
        c.setExposedHeaders(List.of("Authorization", "X-Request-ID"));
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
