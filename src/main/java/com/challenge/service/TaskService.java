package com.challenge.service;

import com.challenge.entity.Tasks;
import com.challenge.exception.TaskNotFoundException;
import com.challenge.mapper.TaskMapper;
import com.challenge.model.TaskDTO;
import com.challenge.repository.TaskRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class TaskService {

    private static final TaskMapper mapper = TaskMapper.INSTANCE;

    @Inject
    TaskRepository taskRepository;

    @Transactional
    public Tasks createTask(TaskDTO taskDTO) {
        validateTaskDTO(taskDTO);
        Tasks task = mapper.toEntity(taskDTO);
        taskRepository.persist(task);
        log.debug("Task created with ID: {}", task.getTaskId());
        return task;
    }

    @Transactional
    public Tasks updateTask(Long id, TaskDTO taskDTO) {
        validateTaskDTO(taskDTO);
        Tasks task = findTaskOrThrow(id);
        task.setTaskName(taskDTO.getTaskName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        taskRepository.persist(task);
        log.debug("Task updated with ID: {}", task.getTaskId());
        return task;
    }

    @Transactional
    public void deleteTask(Long id) {
        Tasks task = findTaskOrThrow(id);
        taskRepository.delete(task);
        log.debug("Task deleted with ID: {}", id);
    }

    public TaskDTO getTaskById(Long id) {
        Tasks task = findTaskOrThrow(id);
        return mapper.toDTO(task);
    }

    public List<TaskDTO> getAllTasks() {
        List<Tasks> tasks = taskRepository.listAll();
        log.info("Retrieved {} tasks", tasks.size());
        return tasks.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    private void validateTaskDTO(TaskDTO taskDTO) {
        if (taskDTO.getTaskName() == null || taskDTO.getTaskName().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be null or empty");
        }
    }

    private Tasks findTaskOrThrow(Long id) {
        return taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }
}
