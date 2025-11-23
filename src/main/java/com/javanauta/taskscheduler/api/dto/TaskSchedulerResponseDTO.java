package com.javanauta.taskscheduler.api.dto;

import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskSchedulerResponseDTO(
        String id,
        String taskName,
        String description,
        LocalDateTime creationDate,
        LocalDateTime scheduledDate,
        LocalDateTime updateDate,
        String userEmail,
        String userId,
        NotificationStatusEnum status
) {}
