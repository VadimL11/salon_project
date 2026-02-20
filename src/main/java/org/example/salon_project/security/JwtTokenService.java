package org.example.salon_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements TokenService {

    private final Key key;

    public JwtTokenService(@Value("${security.jwt.secret}") String secret) {
        // секрет має бути достатньо довгий (для HS256 бажано 32+ символи)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getId(String token) {
        return parseClaims(token).getSubject(); // subject = userId
    }

    @Override
    public RoleType getType(String token) {
        String role = parseClaims(token).get("type", String.class); // "CLIENT"/"MASTER"/"ADMIN"
        return RoleType.valueOf(role);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
