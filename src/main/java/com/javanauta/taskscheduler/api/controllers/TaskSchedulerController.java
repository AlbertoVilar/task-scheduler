package com.javanauta.taskscheduler.api.controllers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.business.TaskService;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskSchedulerController {

    private final TaskService taskService;

    private String extractTokenOrThrow(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header ausente");
        }
        String clean = authorization.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if (clean.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return clean;
    }

    @PostMapping
    public ResponseEntity<TaskSchedulerResponseDTO> createNewTask(
                @RequestHeader("Authorization") String token,
                @RequestBody TaskSchedulerRequestDTO taskDTO) {

        String cleanToken = extractTokenOrThrow(token);

       var responseDTO = taskService.createNewTask(cleanToken, taskDTO);

        // Passa o token limpinho para o serviço
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TaskSchedulerResponseDTO> updateTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody TaskSchedulerRequestDTO taskDTO) {
        String cleanToken = extractTokenOrThrow(token);

        var responseDTO = taskService.updateTask(id, cleanToken, taskDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskSchedulerResponseDTO> findTaskById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String cleanToken = extractTokenOrThrow(token);
        var responseDTO = taskService.findTaskById(id, cleanToken);
        return ResponseEntity.ok(responseDTO);
    }

    // READ BY STATUS (usa query param para evitar ambiguidade com /{id})
    @GetMapping(params = "status")
    public ResponseEntity<List<TaskSchedulerResponseDTO>> findTasksByStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam NotificationStatusEnum status) {
        String cleanToken = extractTokenOrThrow(token);
        return ResponseEntity.ok(taskService.findTasksByStatus(cleanToken, status));
    }

    // READ BY STATUS (usa query param para evitar ambiguidade com /{id})
    @GetMapping(params = {"startDate", "endDate"})
    public ResponseEntity<List<TaskSchedulerResponseDTO>> findTasksByScheduledDate(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate ) {
        String cleanToken = extractTokenOrThrow(token);
        return ResponseEntity.ok(taskService.findTasksByScheduledDate(cleanToken, startDate, endDate));
    }

    @GetMapping
    public ResponseEntity<List<TaskSchedulerResponseDTO>> getAllTasks(
            @RequestHeader("Authorization") String token) {
        String cleanToken = extractTokenOrThrow(token);
        return ResponseEntity.ok(taskService.getAllTasks(cleanToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String cleanToken = extractTokenOrThrow(token);
        taskService.deleteTask(cleanToken, id);

        return ResponseEntity.noContent().build();
    }


}
