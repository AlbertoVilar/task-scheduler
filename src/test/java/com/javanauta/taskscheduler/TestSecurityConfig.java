package com.javanauta.taskscheduler;

import com.javanauta.taskscheduler.infrastructure.security.TokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public TokenService tokenService() {
        return new TokenService() {
            @Override
            public boolean validateToken(String token) {
                return true; // Stub para testes
            }

            @Override
            public String getUsernameFromToken(String token) {
                return "test@example.com";
            }

            @Override
            public String getUserIdFromToken(String token) {
                return "test-user-id";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
                return List.of();
            }
        };
    }
}