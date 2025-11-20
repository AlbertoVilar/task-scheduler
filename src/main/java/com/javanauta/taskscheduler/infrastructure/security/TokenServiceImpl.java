package com.javanauta.taskscheduler.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            // Log a an exception, e.g., MalformedJwtException, ExpiredJwtException, etc.
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public String getUserIdFromToken(String token) {
        Object userId = extractAllClaims(token).get("userId");
        return userId != null ? String.valueOf(userId) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Object rolesClaim = extractAllClaims(token).get("roles");
        if (rolesClaim == null) {
            return Collections.emptyList();
        }

        List<String> roles;
        if (rolesClaim instanceof List) {
            roles = (List<String>) rolesClaim;
        } else if (rolesClaim instanceof String) {
            roles = Arrays.asList(((String) rolesClaim).split(","));
        } else {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(role -> {
                    if (role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority(role);
                    }
                    return new SimpleGrantedAuthority("ROLE_" + role);
                })
                .collect(Collectors.toList());
    }
}
