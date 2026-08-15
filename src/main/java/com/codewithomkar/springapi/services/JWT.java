package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class JWT {
    private final Claims claims;
    private final SecretKey secretKey;

    public JWT(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId() {
        return Long.parseLong(claims.getSubject());
    }

    public Role getRole() {
        var role = claims.get("role", String.class);
        return Role.valueOf(role);
    }

    public String toString() {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();

    }


}
