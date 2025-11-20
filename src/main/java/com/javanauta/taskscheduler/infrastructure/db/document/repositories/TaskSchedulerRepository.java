package com.javanauta.taskscheduler.infrastructure.db.document.repositories;

import com.javanauta.taskscheduler.infrastructure.entity.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSchedulerRepository extends MongoRepository<TaskEntity, String> {
}
