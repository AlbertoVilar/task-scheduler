package com.javanauta.taskscheduler.api.controllers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerDTO;
import com.javanauta.taskscheduler.business.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskSchedulerController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskSchedulerDTO> createNewTask(
                @RequestHeader("Authorization") String token,
                @RequestBody TaskSchedulerDTO taskDTO) {

        // LIMPEZA OBRIGATÓRIA: Remove o "Bearer " e espaços em branco
        String cleanToken = token.replace("Bearer ", "").trim();

       taskDTO = taskService.createNewTask(cleanToken, taskDTO);

        // Passa o token limpinho para o serviço
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTO);

    }

}
