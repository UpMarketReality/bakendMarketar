package com.upc.webmarketar.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.*;
import java.util.*;

@Component
public class JwtTokens {
    private final Key key;
    private final long minutes;

    public JwtTokens(
            @Value("MarketAR_Clave_Secreta_JWT_2026_Seguridad_123456789") String secret,
            @Value("15") long minutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.minutes = minutes;
    }

    public OffsetDateTime expires() {
        return OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(minutes);
    }

    public String issue(String email, OffsetDateTime exp) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer("marketar")
                .setAudience("marketar-api")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(exp.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String email(String token) {
        return Jwts.parserBuilder()
                .requireIssuer("marketar")
                .requireAudience("marketar-api")
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
