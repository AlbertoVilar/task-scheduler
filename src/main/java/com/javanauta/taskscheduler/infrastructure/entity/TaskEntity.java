package com.javanauta.taskscheduler.infrastructure.entity;

import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "task")
public class TaskEntity {

    @Id
    private String id;

    private String taskName;

    private String description;

    @CreatedDate
    private LocalDateTime creationDate;

    private LocalDateTime scheduledDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    private String userEmail; // Mantive para facilitar leitura visual

    private String userId;  // Id que virá do header e sera guradado no banco

    private NotificationStatusEnum status;

}