package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.config.JwtConfig;
import com.codewithomkar.springapi.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.hibernate.boot.internal.Abstract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JWTService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(User userEntity) {
        return getToken(userEntity, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User userEntity) {
        return getToken(userEntity, jwtConfig.getRefreshTokenExpiration());
    }

    private String getToken(User userEntity, long tokenExpiration) {
        return Jwts.builder()
                .subject(userEntity.getId().toString())
                .claim("email", userEntity.getEmail())
                .claim("name", userEntity.getName())
                .claim("role", userEntity.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (JwtException e){
            return false;
        }

    }

    private Claims getClaims(String token) {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    public Long getUserIdFromToken(String authToken) {
        try {
            var claims = getClaims(authToken);
            return Long.valueOf(claims.getSubject());
        }catch (JwtException e){
            return null;
        }
    }
}
