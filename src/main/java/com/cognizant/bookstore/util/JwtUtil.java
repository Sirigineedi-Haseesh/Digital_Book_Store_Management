package com.cognizant.bookstore.util;

import java.nio.charset.StandardCharsets;
//import java.io.charset.StandardCharsets;
import java.util.Date;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "01234567890123456789012345678901"; // Must be at least 32 bytes

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    /**
     * Generates JWT token with username and role.
     */
    public String generateToken(String username, String role) {
    	if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
            System.out.println(role);
        }
        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extracts username from the JWT token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))) // ✅ Use secure key extraction
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Extracts role from the JWT token.
     */
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))) // ✅ Use secure key extraction
            .build()
            .parseClaimsJws(token)
            .getBody();
        

        return claims.get("role", String.class);
    }

    /**
     * Validates JWT token and handles exceptions.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))) // ✅ Use secure key extraction
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            System.out.println("JWT Token validation failed: " + e.getMessage()); // ✅ Improved exception handling
            return false;
        }
    }
}
