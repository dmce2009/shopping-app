package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.config.JwtConfig;
import com.codewithomkar.springapi.entities.Role;
import com.codewithomkar.springapi.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JWTService {

    private final JwtConfig jwtConfig;

    public JWT generateAccessToken(User userEntity) {
        return generateToken(userEntity, jwtConfig.getAccessTokenExpiration());
    }

    public JWT generateRefreshToken(User userEntity) {
        return generateToken(userEntity, jwtConfig.getRefreshTokenExpiration());
    }

    private JWT generateToken(User userEntity, long tokenExpiration) {

        var claims = Jwts.claims().subject(userEntity.getId().toString())
                .add("email", userEntity.getEmail())
                .add("name", userEntity.getName())
                .add("role", userEntity.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000)).build();

        return new JWT(claims, jwtConfig.getSecretKey() );
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (JwtException e){
            return false;
        }

    }

    public JWT parseToken(String token) {
        try{
            var claims = getClaims(token);
            return new JWT(claims, jwtConfig.getSecretKey());
        }catch (JwtException ex){
            return null;
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


}
