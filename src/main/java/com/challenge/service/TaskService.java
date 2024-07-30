package com.challenge.service;

import com.challenge.entity.Tasks;
import com.challenge.mapper.TaskMapper;
import com.challenge.model.TaskDTO;
import com.challenge.repository.TaskRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);
    private static final TaskMapper mapper = TaskMapper.INSTANCE;

    @Inject
    TaskRepository taskRepository;

    @Transactional
    public Tasks createTask(TaskDTO taskDTO) {
        Tasks tasks = mapper.toEntity(taskDTO);
        taskRepository.persist(tasks);
        LOG.debug("Task created with ID: {}", tasks.getTaskId());
        return tasks;
    }

    @Transactional
    public Tasks updateTask(Long id, TaskDTO taskDTO) {
        Optional<Tasks> optionalTask = taskRepository.findByIdOptional(id);
        if (optionalTask.isPresent()) {
            Tasks task = optionalTask.get();
            task.setTaskName(taskDTO.getTaskName());
            task.setDescription(taskDTO.getDescription());
            task.setStatus(taskDTO.getStatus());
            taskRepository.persist(task);
            LOG.debug("Task updated with ID: {}", task.getTaskId());
            return task;
        } else {
            LOG.error("Task with ID {} not found", id);
            throw new RuntimeException("Task not found");
        }
    }

    @Transactional
    public void deleteTask(Long id) {
        Optional<Tasks> optionalTask = taskRepository.findByIdOptional(id);
        if (optionalTask.isPresent()) {
            taskRepository.delete(optionalTask.get());
            LOG.debug("Task deleted with ID: {}", id);
        } else {
            LOG.error("Task with ID {} not found", id);
            throw new RuntimeException("Task not found");
        }
    }

    public TaskDTO getTaskById(Long id) {
        Optional<Tasks> optionalTask = taskRepository.findByIdOptional(id);
        if (optionalTask.isPresent()) {
            return mapper.toDTO(optionalTask.get());
        } else {
            LOG.error("Task with ID {} not found", id);
            throw new RuntimeException("Task not found");
        }
    }

    public List<TaskDTO> getAllTasks() {
        List<Tasks> tasks = taskRepository.listAll();
        Log.info(tasks);
        return tasks.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}
