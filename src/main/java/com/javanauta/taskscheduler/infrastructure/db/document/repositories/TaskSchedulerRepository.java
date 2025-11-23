package com.javanauta.taskscheduler.infrastructure.db.document.repositories;

import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import com.javanauta.taskscheduler.infrastructure.enums.NotificationStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskSchedulerRepository extends MongoRepository<TaskEntity, String> {

    // Lista por Status
    List<TaskEntity> findByStatus(NotificationStatusEnum status);

    List<TaskEntity> findByScheduledDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
