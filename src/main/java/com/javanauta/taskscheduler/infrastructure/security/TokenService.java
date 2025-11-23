package com.javanauta.taskscheduler.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface TokenService {

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

    String getUserIdFromToken(String token);

    Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token);
}
