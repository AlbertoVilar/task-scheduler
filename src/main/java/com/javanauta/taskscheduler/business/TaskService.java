package com.javanauta.taskscheduler.business;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.infrastructure.db.document.repositories.TaskSchedulerRepository;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import com.javanauta.taskscheduler.infrastructure.security.TokenService;
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
    private final TokenService tokenService;
    private final AccessGuard accessGuard;

    /**
     * Obtém o userId a partir do token JWT e valida sua presença.
     * <p>
     * Regras:
     * - Extrai o claim de userId do token.
     * - Lança 401 (UNAUTHORIZED) quando o token é nulo, vazio ou não contém userId.
     *
     * @param token JWT já limpo (sem prefixo Bearer)
     * @return userId válido
     * @throws org.springframework.web.server.ResponseStatusException 401 quando token/userId inválido
     */
    private String getUserIdOrThrow(String token) {
        String userId = tokenService.getUserIdFromToken(token);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou ausente");
        }
        return userId;
    }


    // CREATE
    public TaskSchedulerResponseDTO createNewTask(String token, TaskSchedulerRequestDTO taskDTO) {

        if (taskDTO == null) {
            throw new IllegalArgumentException("O objeto da requisição (taskDTO) não pode ser nulo.");
        }

        // 1. Extrai o dono da tarefa
        String userEmail = tokenService.getUsernameFromToken(token);
        String userId = getUserIdOrThrow(token);

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

        String userId = getUserIdOrThrow(token);
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
    public TaskSchedulerResponseDTO findTaskById(String id, String token) {

        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Id não pode ser nulo ou vazio.");
        }

        var task = schedulerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        String userId = getUserIdOrThrow(token);
        accessGuard.ensureOwner(task, userId);

        return converter.toDTO(task);

    }

    // READ BY STATUS
    public List<TaskSchedulerResponseDTO> findTasksByStatus(String token, NotificationStatusEnum status) {

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Status não pode ser nulo ou vazio.");
        }

        String userId = getUserIdOrThrow(token);
        List<TaskEntity> entities = schedulerRepository.findByUserIdAndStatus(userId, status);
        if (entities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma task encontrada com o status informado.");
        }

        return entities.stream().map(converter::toDTO).toList();

    }

    // LIST BY START AND END DATE
    public List<TaskSchedulerResponseDTO> findTasksByScheduledDate(String token,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("As datas não podem ser nulas.");

        }
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime startDateTime = startDate.atStartOfDay(zone).toLocalDateTime();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX).atZone(zone).toLocalDateTime();

        String userId = getUserIdOrThrow(token);
        List<TaskEntity> tasks = schedulerRepository.findByUserIdAndScheduledDateBetween(userId, startDateTime, endDateTime);

        return tasks.stream().map(converter::toDTO).toList();
    }

    // LIST
    public List<TaskSchedulerResponseDTO> getAllTasks(String token) {
        String userId = getUserIdOrThrow(token);
        List<TaskEntity> tasks = schedulerRepository.findByUserId(userId);
        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma task encontrada com o status informado.");
        }

        return tasks.stream().map(converter::toDTO).toList();
    }

    // DELETE
    public void deleteTask(String token, String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id não pode ser nulo ou vazio");
        }

        String userId = getUserIdOrThrow(token);

        var task = schedulerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        accessGuard.ensureOwner(task, userId);

        schedulerRepository.delete(task);
    }

}
