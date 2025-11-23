package com.javanauta.taskscheduler.business;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.infrastructure.db.document.repositories.TaskSchedulerRepository;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import com.javanauta.taskscheduler.mappers.TaskSchedulerConverter;
import com.javanauta.taskscheduler.business.security.AccessGuard;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;


@Service
@AllArgsConstructor
public class TaskService {

    private final TaskSchedulerRepository schedulerRepository;
    private final TaskSchedulerConverter converter;
    private final AccessGuard accessGuard;

    // CREATE
    public TaskSchedulerResponseDTO createNewTask(String userId, TaskSchedulerRequestDTO taskDTO) {

        if (taskDTO == null) {
            throw new IllegalArgumentException("O objeto da requisição (taskDTO) não pode ser nulo.");
        }

        // 1. Converte o DTO para Entidade
        var taskEntity = converter.toEntity(taskDTO);

        // 2. Define o dono da tarefa a partir do userId já validado na Controller
        taskEntity.setUserId(userId);

        // 3. Salva a entidade completa
        var taskSaved = schedulerRepository.save(taskEntity);

        return converter.toDTO(taskSaved);
    }

    // UPDATE
    public TaskSchedulerResponseDTO updateTask(String id,
                                               String userId,
                                               TaskSchedulerRequestDTO taskDTO) {

        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O Id não pode ser nulo ou vazio.");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        accessGuard.ensureOwner(task, userId);

        try {
            converter.updateTaskEntity(task, taskDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        var updated = schedulerRepository.save(task);
        return converter.toDTO(updated);
    }

    // READ BY ID
    public TaskSchedulerResponseDTO findTaskById(String id, String userId) {

        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Id não pode ser nulo ou vazio.");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        accessGuard.ensureOwner(task, userId);

        return converter.toDTO(task);

    }

    // READ BY STATUS
    public List<TaskSchedulerResponseDTO> findTasksByStatus(String userId, NotificationStatusEnum status) {

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Status não pode ser nulo ou vazio.");
        }

        List<TaskEntity> entities = schedulerRepository.findByUserIdAndStatus(userId, status);
        if (entities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma task encontrada com o status informado.");
        }

        return entities.stream().map(converter::toDTO).toList();

    }

    // LIST BY START AND END DATE
    public List<TaskSchedulerResponseDTO> findTasksByScheduledDate(String userId,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("As datas não podem ser nulas.");

        }
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime startDateTime = startDate.atStartOfDay(zone).toLocalDateTime();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX).atZone(zone).toLocalDateTime();

        List<TaskEntity> tasks = schedulerRepository.findByUserIdAndScheduledDateBetween(userId, startDateTime, endDateTime);

        return tasks.stream().map(converter::toDTO).toList();
    }

    // LIST
    public List<TaskSchedulerResponseDTO> getAllTasks(String userId) {
        List<TaskEntity> tasks = schedulerRepository.findByUserId(userId);
        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma task encontrada com o status informado.");
        }

        return tasks.stream().map(converter::toDTO).toList();
    }

    // DELETE
    public void deleteTask(String userId, String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id não pode ser nulo ou vazio");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        accessGuard.ensureOwner(task, userId);

        schedulerRepository.delete(task);
    }

    

}
