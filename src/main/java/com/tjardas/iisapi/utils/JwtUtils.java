package com.tjardas.iisapi.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private final String jwtSecret = "b92938967a3c672f1ce07a1fcfbe816a570f53d4d17fd44604d1be9d2297900d7b4c57d012bb8c64007e2af558a469f095ac930006182ba7eb9605de1fe4a2a2ceaa277a98914f0e283038588a22b169d624913ed907b570d1e2955f3fd8f40a5b52ea58c1dfda9528d6954167d6e03e2971bc085b506ed9e36b41f9bc4d31dc7f56346aea51fd69818e1b763a9c0a8f5b17c64b7c200f24fce501ef2a18bc8ec95a4543f10301ee567e619faab1d6f341ecee1d0c44d09ff9bf62ec977050e002d99f5441c9c01861546a31e183401905d03829d43829ef32d040e29cc2568645de863e8a68cad82bf2ea15623f0ad82373f16a3c62763f0812983180948019";

    private final long accessTokenExpirationMs = 15 * 60 * 1000; // 15 minutes
    private final long refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpirationMs);
    }

    private String generateToken(String username, long expirationMs) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }
}

