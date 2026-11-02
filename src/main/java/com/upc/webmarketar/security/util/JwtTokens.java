package com.upc.webmarketar.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtTokens {
    private final Key key;
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
