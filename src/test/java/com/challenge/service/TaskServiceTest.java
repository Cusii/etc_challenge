package com.challenge.service;

import com.challenge.entity.Tasks;
import com.challenge.exception.TaskNotFoundException;
import com.challenge.mapper.TaskMapper;
import com.challenge.model.TaskDTO;
import com.challenge.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private TaskDTO taskDTO;
    private Tasks task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskDTO = new TaskDTO();
        taskDTO.setTaskId(1L);
        taskDTO.setTaskName("Task Name");
        taskDTO.setDescription("Description");
        taskDTO.setStatus("pending");

        task = new Tasks();
        task.setTaskId(1L);
        task.setTaskName("Task Name");
        task.setDescription("Description");
        task.setStatus("pending");
    }

    @Test
    void testCreateTask_Success() {
        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(task);
        doNothing().when(taskRepository).persist(any(Tasks.class));
        when(taskMapper.toDTO(any(Tasks.class))).thenReturn(taskDTO);

        Tasks createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals("Task Name", createdTask.getTaskName());
        verify(taskRepository, times(1)).persist(any(Tasks.class));
    }

    @Test
    void testCreateTask_InvalidTaskDTO() {
        TaskDTO invalidTaskDTO = new TaskDTO();
        invalidTaskDTO.setTaskId(1L);
        invalidTaskDTO.setDescription("Description");
        invalidTaskDTO.setStatus("pending");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(invalidTaskDTO);
        });

        assertEquals("Task name cannot be null or empty", thrown.getMessage());
        verify(taskRepository, times(0)).persist(any(Tasks.class));
    }

    @Test
    void testUpdateTask_Success() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(task);
        doNothing().when(taskRepository).persist(any(Tasks.class));

        Tasks updatedTask = taskService.updateTask(1L, taskDTO);

        assertNotNull(updatedTask);
        assertEquals("Task Name", updatedTask.getTaskName());
        verify(taskRepository, times(1)).persist(any(Tasks.class));
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(1L, taskDTO);
        });

        assertEquals("Task with ID 1 not found", thrown.getMessage());
        verify(taskRepository, times(0)).persist(any(Tasks.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(any(Tasks.class));

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).delete(any(Tasks.class));
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(1L);
        });

        assertEquals("Task with ID 1 not found", thrown.getMessage());
        verify(taskRepository, times(0)).delete(any(Tasks.class));
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(any(Tasks.class))).thenReturn(taskDTO);

        TaskDTO result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Task Name", result.getTaskName());
    }

    @Test
    void testGetTaskById_TaskNotFound() {
        when(taskRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });

        assertEquals("Task with ID 1 not found", thrown.getMessage());
    }

    @Test
    void testGetAllTasks_Success() {
        List<Tasks> tasks = Stream.of(task).collect(Collectors.toList());
        List<TaskDTO> taskDTOs = Stream.of(taskDTO).collect(Collectors.toList());

        when(taskRepository.listAll()).thenReturn(tasks);
        when(taskMapper.toDTO(any(Tasks.class))).thenReturn(taskDTO);

        List<TaskDTO> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task Name", result.get(0).getTaskName());
    }
}
