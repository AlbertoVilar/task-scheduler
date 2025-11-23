package com.javanauta.taskscheduler.api.controllers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.business.TaskService;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import com.javanauta.taskscheduler.security.TokenCleaner;
import com.javanauta.taskscheduler.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskSchedulerController {

    private final TaskService taskService;
    private final TokenCleaner tokenCleaner;
    private final SecurityService securityService;

    private String extractUser(String token) {
        return securityService.extractUserIdOrThrow(tokenCleaner.clean(token));
    }

    @PostMapping
    public ResponseEntity<TaskSchedulerResponseDTO> createNewTask(
                @RequestHeader("Authorization") String token,
                @RequestBody TaskSchedulerRequestDTO taskDTO) {

        String userId = extractUser(token);

        var responseDTO = taskService.createNewTask(userId, taskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TaskSchedulerResponseDTO> updateTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody TaskSchedulerRequestDTO taskDTO) {
        String userId = extractUser(token);

        var responseDTO = taskService.updateTask(id, userId, taskDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskSchedulerResponseDTO> findTaskById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String userId = extractUser(token);
        var responseDTO = taskService.findTaskById(id, userId);
        return ResponseEntity.ok(responseDTO);
    }

    // READ BY STATUS (usa query param para evitar ambiguidade com /{id})
    @GetMapping(params = "status")
    public ResponseEntity<List<TaskSchedulerResponseDTO>> findTasksByStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam NotificationStatusEnum status) {
        String userId = extractUser(token);
        return ResponseEntity.ok(taskService.findTasksByStatus(userId, status));
    }

    // READ BY STATUS (usa query param para evitar ambiguidade com /{id})
    @GetMapping(params = {"startDate", "endDate"})
    public ResponseEntity<List<TaskSchedulerResponseDTO>> findTasksByScheduledDate(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate ) {
        String userId = extractUser(token);
        return ResponseEntity.ok(taskService.findTasksByScheduledDate(userId, startDate, endDate));
    }

    @GetMapping
    public ResponseEntity<List<TaskSchedulerResponseDTO>> getAllTasks(
            @RequestHeader("Authorization") String token) {
        String userId = extractUser(token);
        return ResponseEntity.ok(taskService.getAllTasks(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String userId = extractUser(token);
        taskService.deleteTask(userId, id);

        return ResponseEntity.noContent().build();
    }


}
