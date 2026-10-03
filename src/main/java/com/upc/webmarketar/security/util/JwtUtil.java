package com.upc.webmarketar.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY =
            "MarketAR_Clave_Secreta_JWT_2026_Seguridad_123456789";

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    private Key obtenerClaveFirma() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(obtenerClaveFirma(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerCorreo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerClaveFirma())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Date extraerExpiracion(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerClaveFirma())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    private boolean estaTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        String correo = extraerCorreo(token);

        return correo.equals(userDetails.getUsername())
                && !estaTokenExpirado(token);
    }
}