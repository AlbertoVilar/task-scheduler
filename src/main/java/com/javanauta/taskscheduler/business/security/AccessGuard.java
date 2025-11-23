package com.javanauta.taskscheduler.business.security;

import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.exception.ForbiddenAccessException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Componente de regras de acesso/ownership.
 * Responsável por garantir que a entidade pertence ao usuário autenticado
 * e permitir bypass para administradores quando aplicável.
 */
@Component
public class AccessGuard {

    /**
     * Asserta que a tarefa pertence ao usuário informado.
     * Lança ForbiddenAccessException (HTTP 403) caso contrário.
     */
    public void assertOwner(TaskEntity task, String userId) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser nula no AccessGuard");
        }
        if (!Objects.equals(task.getUserId(), userId)) {
            throw new ForbiddenAccessException(
                    "Você não tem permissão para acessar a tarefa id="
                            + task.getId() + " (owner=" + task.getUserId() + ", user=" + userId + ")"
            );
        }
    }

    /**
     * Asserta que a tarefa pertence ao usuário, permitindo bypass quando isAdmin=true.
     * Útil para cenários futuros com ROLE_ADMIN.
     */
    public void assertOwnerOrAdmin(TaskEntity task, String userId, boolean isAdmin) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser nula no AccessGuard");
        }
        if (!isAdmin && !Objects.equals(task.getUserId(), userId)) {
            throw new ForbiddenAccessException(
                    "Acesso negado à tarefa id=" + task.getId() +
                            " (owner=" + task.getUserId() + ", user=" + userId + ", isAdmin=" + isAdmin + ")"
            );
        }
    }
}