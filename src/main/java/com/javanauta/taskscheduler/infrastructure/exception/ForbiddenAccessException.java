package com.javanauta.taskscheduler.infrastructure.exception;

/**
 * Exceção de acesso negado para operações em entidades que não pertencem
 * ao usuário autenticado. Deve ser mapeada para HTTP 403 pelo handler global.
 */
public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}