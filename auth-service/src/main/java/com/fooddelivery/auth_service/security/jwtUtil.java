package com.fooddelivery.auth_service.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class jwtUtil {

    // 1. Inject the secret string from application.properties
    @Value("${jwt.secret}")
    private String secret;
    
    private Key key;
    private final long EXPIRATION_TIME = 86400000; // 24 hours

    // 2. This method builds the cryptographic Key object as soon as the app boots up
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate Token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Extract the user's email (subject) from the token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token is valid
    public boolean isTokenValid(String token, String userEmail) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(userEmail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}