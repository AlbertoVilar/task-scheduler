package com.javanauta.taskscheduler.business;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.infrastructure.db.document.repositories.TaskSchedulerRepository;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.security.CustomUserDetails;
import com.javanauta.taskscheduler.infrastructure.security.TokenService;
import com.javanauta.taskscheduler.mappers.TaskSchedulerConverter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskSchedulerRepository schedulerRepository;
    private final TaskSchedulerConverter converter;
    private final TokenService tokenService;

    // CREATE
    public TaskSchedulerResponseDTO createNewTask(String token, TaskSchedulerRequestDTO taskDTO) {

        if (taskDTO == null) {
            throw new IllegalArgumentException("O objeto da requisição (taskDTO) não pode ser nulo.");
        }

        // 1. Extrai o dono da tarefa
        String userEmail = tokenService.getUsernameFromToken(token);
        String userId = tokenService.getUserIdFromToken(token);

        // 2. Converte o DTO para Entidade
        var taskEntity = converter.toEntity(taskDTO);

        taskEntity.setUserEmail(userEmail);
        taskEntity.setUserId(userId);

        // 4. Agora sim, salva a entidade completa
        var taskSaved = schedulerRepository.save(taskEntity);

        return converter.toDTO(taskSaved);
    }

    // UPDATE
    public TaskSchedulerResponseDTO updateTask(String id,
                                               String token,
                                               TaskSchedulerRequestDTO taskDTO) {

        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O Id não pode ser nulo ou vazio.");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        String userId = tokenService.getUserIdFromToken(token);

        if (!task.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para atualizar esta tarefa"
            );
        }

        try {
            converter.updateTaskEntity(task, taskDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        var updated = schedulerRepository.save(task);
        return converter.toDTO(updated);
    }

    // READ BY ID
    public TaskSchedulerResponseDTO findTaskById(String id) {

        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Id não pode ser nulo ou vazio.");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        return converter.toDTO(task);

    }

    // LIST
    public List<TaskSchedulerResponseDTO> getAllTasks() {

        List<TaskEntity> entities = schedulerRepository.findAll();
        return entities.stream().map(converter::toDTO).toList();
    }

    // DELETE
    public void deleteTask(String token, String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id não pode ser nulo ou vazio");
        }

        String userId = tokenService.getUserIdFromToken(token);

        var task = schedulerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        if (!task.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para deletar esta tarefa"
            );
        }

        schedulerRepository.delete(task);
    }

}
