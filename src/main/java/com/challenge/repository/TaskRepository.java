package com.challenge.repository;

import com.challenge.entity.TaskEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<TaskEntity> {
}
