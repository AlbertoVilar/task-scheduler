package com.javanauta.taskscheduler.mappers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerRequestDTO;
import com.javanauta.taskscheduler.api.dto.TaskSchedulerResponseDTO;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import org.springframework.stereotype.Component;


@Component
public class TaskSchedulerConverter { // 1. Corrigido o nome (Converter)

    public TaskEntity toEntity(TaskSchedulerRequestDTO dto) {
        TaskEntity entity = new TaskEntity();
        entity.setTaskName(dto.taskName());
        entity.setDescription(dto.description());
        entity.setScheduledDate(dto.scheduledDate());
        entity.setStatus(NotificationStatusEnum.PENDING);
        return entity;
    }

    public void updateTaskEntity (TaskEntity entity, TaskSchedulerRequestDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("O objeto da requisição (dto) não pode ser nulo.");
        }
        entity.setTaskName( (dto.taskName() != null) ? dto.taskName() : entity.getTaskName());
        entity.setDescription( (dto.description() != null) ? dto.description() : entity.getDescription());
        entity.setScheduledDate( (dto.scheduledDate() != null) ? dto.scheduledDate() : entity.getScheduledDate());

    }

    public TaskSchedulerResponseDTO toDTO(TaskEntity entity) {
        return TaskSchedulerResponseDTO.builder()
                .id(entity.getId())
                .taskName(entity.getTaskName())
                .description(entity.getDescription())
                .creationDate(entity.getCreationDate())
                .scheduledDate(entity.getScheduledDate())
                .updateDate(entity.getUpdateDate())
                .userEmail(entity.getUserEmail())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .build();
        }

}