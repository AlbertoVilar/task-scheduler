package com.javanauta.taskscheduler.api.controllers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.business.TaskService;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskSchedulerController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskSchedulerResponseDTO> createNewTask(
                @RequestHeader("Authorization") String token,
                @RequestBody TaskSchedulerRequestDTO taskDTO) {

        // LIMPEZA OBRIGATÓRIA: Remove o "Bearer " e espaços em branco
        String cleanToken = token.replace("Bearer ", "").trim();

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

        // LIMPEZA OBRIGATÓRIA: Remove o "Bearer " e espaços em branco
        String cleanToken = token.replace("Bearer ", "").trim();

        var responseDTO = taskService.updateTask(id, cleanToken, taskDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskSchedulerResponseDTO> findTaskById(@PathVariable String id) {
        var responseDTO = taskService.findTaskById(id);
        return ResponseEntity.ok(responseDTO);
    }

    // READ BY STATUS (usa query param para evitar ambiguidade com /{id})
    @GetMapping(params = "status")
    public ResponseEntity<List<TaskSchedulerResponseDTO>> findTasksByStatus(
            @RequestParam NotificationStatusEnum status) {

        return ResponseEntity.ok(taskService.findTasksByStatus(status));
    }

    @GetMapping
    public ResponseEntity<List<TaskSchedulerResponseDTO>> getAllTasks() {

        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {

        String cleanToken = token.replace("Bearer ", "").trim();
        taskService.deleteTask(cleanToken, id);

        return ResponseEntity.noContent().build();
    }


}
