package com.javanauta.taskscheduler.mappers;

import com.javanauta.taskscheduler.api.dto.TaskSchedulerDTO;
import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import org.springframework.stereotype.Component;


@Component
public class TaskSchedulerConverter { // 1. Corrigido o nome (Converter)


    public TaskEntity toEntity(TaskSchedulerDTO dto) {
        return TaskEntity.builder()

                .taskName(dto.taskName())
                .description(dto.description())
                .scheduledDate(dto.scheduledDate())
                .userEmail(dto.userEmail())
                .status(NotificationStatusEnum.PENDING)
                .build();
    }

    public TaskSchedulerDTO toDTO(TaskEntity entity) {

        return TaskSchedulerDTO.builder()
                .id(entity.getId())
                .taskName(entity.getTaskName())
                .description(entity.getDescription())
                .creationDate(entity.getCreationDate())
                .scheduledDate(entity.getScheduledDate())
                .updateDate(entity.getUpdateDate())
                .userEmail(entity.getUserEmail())
                .status(entity.getStatus())
                .build();
        }

}