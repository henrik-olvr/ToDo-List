package com.henrikolvr.todolist;

import com.henrikolvr.todolist.controller.TaskController;
import com.henrikolvr.todolist.model.Task;
import com.henrikolvr.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskRepository taskRepository;

    List<Task> tasks = new ArrayList<>();

    @BeforeEach
    void setUp() {
        tasks = List.of(
                new Task(1L, "Fulano", "Trabalhar", "ToDo"),
                new Task(2L, "Fulano", "Estudar", "Done")
        );
    }

    @Test
    void shouldReturnAllTasks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "owner":"Fulano",
                        "description":"Trabalhar",
                        "currentstatus":"ToDo"
                    },
                    {
                        "id":2,
                        "owner":"Fulano",
                        "description":"Estudar",
                        "currentstatus":"Done"
                    }
                ]
                """;

        when(taskRepository.findAll()).thenReturn(tasks);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldReturnTaskWhenGivenValidId() throws Exception {
        String jsonResponse = """
                {
                    "id":1,
                    "owner":"Fulano",
                    "description":"Trabalhar",
                    "currentstatus":"ToDo"
                    }
                """;

        when(taskRepository.findById(1L)).thenReturn(Optional.of(tasks.get(0)));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/tasks/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldReturnToDoTasks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "owner":"Fulano",
                        "description":"Trabalhar",
                        "currentstatus":"ToDo"
                    }
                ]
                """;

        when(taskRepository.findByCurrentstatus("ToDo")).thenReturn(List.of(tasks.get(0)));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/tasks/ToDo"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldReturnDoneTasks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":2,
                        "owner":"Fulano",
                        "description":"Estudar",
                        "currentstatus":"Done"
                    }
                ]
                """;

        when(taskRepository.findByCurrentstatus("Done")).thenReturn(List.of(tasks.get(1)));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/tasks/Done"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldReturnTasksWhenGivenOwner() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "owner":"Fulano",
                        "description":"Trabalhar",
                        "currentstatus":"ToDo"
                    },
                    {
                        "id":2,
                        "owner":"Fulano",
                        "description":"Estudar",
                        "currentstatus":"Done"
                    }
                ]
                """;

        when(taskRepository.findByOwner("Fulano")).thenReturn(tasks);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/tasks/owner/Fulano"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldUpdateTaskWhenGivenValidId() throws Exception {
        Task updatedTask = new Task(1L, "Fulano", "Trabalhar", "Done");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(tasks.get(0)));
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);

        String jsonResponse = """
                {
                    "id":1,
                    "owner":"Fulano",
                    "description":"Trabalhar",
                    "currentstatus":"Done"
                }
                """;

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType("application/json")
                .content(jsonResponse))
                .andExpect(status().isOk());

    }

    @Test
    void shouldDeleteTaskWhenGivenValidId() throws Exception {
        doNothing().when(taskRepository).deleteById(2L);

        mockMvc.perform(delete("/api/v1/tasks/2"))
                .andExpect(status().isNoContent());

        verify(taskRepository, times(1)).deleteById(2L);
    }
}
