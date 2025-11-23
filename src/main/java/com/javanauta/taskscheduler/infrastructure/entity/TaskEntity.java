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

    // Explicit getters to ensure compatibility across environments
    public String getId() {
        return this.id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public LocalDateTime getScheduledDate() {
        return this.scheduledDate;
    }

    public LocalDateTime getUpdateDate() {
        return this.updateDate;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public NotificationStatusEnum getStatus() {
        return this.status;
    }

    public String getUserId() {
        return this.userId;
    }

    // Explicit setters used in converter when Lombok isn't available
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setStatus(NotificationStatusEnum status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}