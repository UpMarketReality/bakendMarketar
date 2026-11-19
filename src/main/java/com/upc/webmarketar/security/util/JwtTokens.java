package com.upc.webmarketar.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtTokens {
    private final SecretKey key;
    private final long minutes;

    public JwtTokens(
            @Value("${marketar.jwt.secret}") String secret,
            @Value("${marketar.jwt.minutes}") long minutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.minutes = minutes;
    }

    public OffsetDateTime expires() {
        return OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(minutes);
    }

    // Generar JWT
    public String issue(String email, OffsetDateTime exp) {
        return Jwts.builder()
                .subject(email)
                .issuer("marketar")
                .audience()
                .add("marketar-api")
                .and()
                .issuedAt(new Date())
                .expiration(Date.from(exp.toInstant()))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // Extraer el correo y validar el JWT
    public String email(String token) {
        return Jwts.parser()
                .requireIssuer("marketar")
                .requireAudience("marketar-api")
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
