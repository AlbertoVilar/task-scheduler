package com.javanauta.taskscheduler.business;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerDTO;
import com.javanauta.taskscheduler.infrastructure.db.document.repositories.TaskSchedulerRepository;
import com.javanauta.taskscheduler.infrastructure.security.TokenService;
import com.javanauta.taskscheduler.mappers.TaskSchedulerConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskSchedulerRepository schedulerRepository;
    private final TaskSchedulerConverter converter;
    private final TokenService tokenService;


    public TaskSchedulerDTO createNewTask(String token, TaskSchedulerDTO taskDTO) {

        if (taskDTO == null) {
            throw new IllegalArgumentException("O objeto da requisição (taskDTO) não pode ser nulo.");
        }

        // 1. Extrai o dono da tarefa
        String userEmail = tokenService.getUsernameFromToken(token); // Mudei o nome da variável

        String userId = tokenService.getUserIdFromToken(token);

        // 2. Converte o DTO para Entidade
        var taskEntity = converter.toEntity(taskDTO);

        taskEntity.setUserEmail(userEmail);
        taskEntity.setUserId(userId);

        // 4. Agora sim, salva a entidade completa
        var taskSaved = schedulerRepository.save(taskEntity);

        return converter.toDTO(taskSaved);

    }
}
