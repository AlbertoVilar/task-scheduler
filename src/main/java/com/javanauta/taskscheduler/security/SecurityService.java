package com.javanauta.taskscheduler.security;

import com.javanauta.taskscheduler.infrastructure.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SecurityService {

    private final TokenService tokenService;

    public SecurityService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Extrai o userId do token JWT e valida sua presença.
     * Lança 401 (UNAUTHORIZED) quando o token é nulo, vazio ou não contém userId.
     */
    public String extractUserIdOrThrow(String token) {
        String userId = tokenService.getUserIdFromToken(token);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou ausente");
        }
        return userId;
    }

    /**
     * Extrai o username/email (subject) do token JWT e valida sua presença.
     * Lança 401 (UNAUTHORIZED) quando o token é nulo, vazio ou não contém username.
     */
    public String extractUsernameOrThrow(String token) {
        String username = tokenService.getUsernameFromToken(token);
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou ausente");
        }
        return username;
    }
}