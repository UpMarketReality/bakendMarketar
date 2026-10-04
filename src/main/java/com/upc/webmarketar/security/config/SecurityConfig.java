package com.upc.webmarketar.security.config;

import com.upc.webmarketar.security.filters.JwtFilter;
import com.upc.webmarketar.security.services.CustomUserDetailsService;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    AuthenticationManager authenticationManager(
            CustomUserDetailsService users, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain chain(HttpSecurity http, JwtFilter filter, CorsConfigurationSource cors)
            throws Exception {
        return http.csrf(c -> c.disable())
                .cors(c -> c.configurationSource(cors))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        a ->
                                a.requestMatchers(
                                                "/api/v1/auth/login",
                                                "/api/v1/auth/register",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**",
                                                "/error")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/v1/products",
                                                "/api/v1/products/*",
                                                "/api/v1/product-categories",
                                                "/api/v1/prototype-categories",
                                                "/media/products/**")
                                        .permitAll()
                                        .requestMatchers("/api/v1/seller/**")
                                        .hasRole("VENDEDOR")
                                        .requestMatchers("/api/v1/prototypes/*/images/*")
                                        .authenticated()
                                        .requestMatchers(
                                                "/api/v1/prototypes/**",
                                                "/api/v1/users/me/favorites/**",
                                                "/api/v1/cart/**",
                                                "/api/v1/quote-requests/**",
                                                "/api/v1/offers/**",
                                                "/api/v1/orders/**",
                                                "/api/v1/purchases/**")
                                        .hasRole("COMPRADOR")
                                        .anyRequest()
                                        .denyAll())
                .exceptionHandling(
                        e ->
                                e.authenticationEntryPoint(
                                                (q, r, x) -> {
                                                    r.setStatus(401);
                                                    r.setContentType(
                                                            "application/json;charset=UTF-8");
                                                    r.getWriter()
                                                            .write(
                                                                    "{\"status\":401,\"message\":\"Inicia"
                                                                            + " sesión\"}");
                                                })
                                        .accessDeniedHandler(
                                                (q, r, x) -> {
                                                    r.setStatus(403);
                                                    r.setContentType(
                                                            "application/json;charset=UTF-8");
                                                    r.getWriter()
                                                            .write(
                                                                    "{\"status\":403,\"message\":\"Acceso"
                                                                            + " denegado\"}");
                                                }))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
