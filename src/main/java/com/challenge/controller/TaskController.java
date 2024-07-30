package com.challenge.controller;

import com.challenge.model.TaskDTO;
import com.challenge.service.TaskService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskController {
    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Inject
    TaskService taskService;

    @POST
    @Path("/create")
    public Response taskCreate(TaskDTO taskDTO) {
        LOG.info("Trying to create {}", taskDTO);
        try {
            taskService.createTask(taskDTO);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            LOG.error("Error creating task: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"Error al crear la tarea.\"}").build();
        }
    }

    @PUT
    @Path("/update/{id}")
    public Response taskUpdate(@PathParam("id") Long id, TaskDTO taskDTO) {
        LOG.info("Trying to update task with ID {}", id);
        try {
            taskService.updateTask(id, taskDTO);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error updating task: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"Error al actualizar la tarea.\"}").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    public Response taskDelete(@PathParam("id") Long id) {
        LOG.info("Trying to delete task with ID {}", id);
        try {
            taskService.deleteTask(id);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error deleting task: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Error al eliminar la tarea.\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTaskById(@PathParam("id") Long id) {
        LOG.info("Trying to get task with ID {}", id);
        try {
            TaskDTO taskDTO = taskService.getTaskById(id);
            return Response.ok(taskDTO).build();
        } catch (Exception e) {
            LOG.error("Error retrieving task: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Tarea no encontrada.\"}").build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllTasks() {
        LOG.info("Trying to get all tasks");
        try {
            List<TaskDTO> tasks = taskService.getAllTasks();
            return Response.ok(tasks).build();
        } catch (Exception e) {
            LOG.error("Error retrieving tasks: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Error al obtener las tareas.\"}").build();
        }
    }
}
