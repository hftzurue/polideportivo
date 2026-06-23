package com.polideportivo.polideportivo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "clave_super_secreta_para_jwt_polideportivo_2026_123456";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Integer idUsuario, String nombreUsuario, String rol) {
        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim("nombreUsuario", nombreUsuario)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}