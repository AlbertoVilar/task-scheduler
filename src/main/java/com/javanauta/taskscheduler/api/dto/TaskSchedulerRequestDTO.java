package com.javanauta.taskscheduler.api.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TaskSchedulerRequestDTO(
        String taskName,
        String description,
        LocalDateTime scheduledDate
) {}
