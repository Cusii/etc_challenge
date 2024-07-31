package com.challenge.controller;

import com.challenge.exception.TaskNotFoundException;
import com.challenge.model.ErrorResponse;
import com.challenge.model.TaskDTO;
import com.challenge.service.TaskService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTaskCreate_Success() {
        TaskDTO taskDTO = new TaskDTO();
        Response response = taskController.taskCreate(taskDTO);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testTaskCreate_Failure() {
        TaskDTO taskDTO = new TaskDTO();
        doThrow(new RuntimeException("Error al crear la tarea.")).when(taskService).createTask(taskDTO);
        Response response = taskController.taskCreate(taskDTO);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Error al crear la tarea.", errorResponse.getMessage());
    }

    @Test
    void testTaskUpdate_Success() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();
        Response response = taskController.taskUpdate(taskId, taskDTO);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testTaskUpdate_NotFound() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();
        doThrow(new TaskNotFoundException("Tarea no encontrada.")).when(taskService).updateTask(taskId, taskDTO);
        Response response = taskController.taskUpdate(taskId, taskDTO);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Tarea no encontrada.", errorResponse.getMessage());
    }

    @Test
    void testTaskDelete_Success() {
        Long taskId = 1L;
        Response response = taskController.taskDelete(taskId);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testTaskDelete_NotFound() {
        Long taskId = 1L;
        doThrow(new TaskNotFoundException("Tarea no encontrada.")).when(taskService).deleteTask(taskId);
        Response response = taskController.taskDelete(taskId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Tarea no encontrada.", errorResponse.getMessage());
    }

    @Test
    void testGetTaskById_Success() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.getTaskById(taskId)).thenReturn(taskDTO);
        Response response = taskController.getTaskById(taskId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(taskDTO, response.getEntity());
    }

    @Test
    void testGetTaskById_NotFound() {
        Long taskId = 1L;
        doThrow(new TaskNotFoundException("Tarea no encontrada.")).when(taskService).getTaskById(taskId);
        Response response = taskController.getTaskById(taskId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Tarea no encontrada.", errorResponse.getMessage());
    }

    @Test
    void testGetAllTasks_Success() {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(taskDTO));
        Response response = taskController.getAllTasks();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).contains(taskDTO));
    }

    @Test
    void testGetAllTasks_Failure() {
        doThrow(new RuntimeException("Error al obtener las tareas.")).when(taskService).getAllTasks();
        Response response = taskController.getAllTasks();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Error al obtener las tareas.", errorResponse.getMessage());
    }
}
