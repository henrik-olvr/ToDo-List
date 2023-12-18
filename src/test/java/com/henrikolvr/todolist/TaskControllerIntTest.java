package com.henrikolvr.todolist;

import com.henrikolvr.todolist.config.ContainersEnvironment;
import com.henrikolvr.todolist.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class TaskControllerIntTest extends ContainersEnvironment {

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        Task task = new Task(1L, "Fulano", "Trabalhar", "Done");
        Task task1 = new Task(2l, "Fulano", "Estudar", "ToDo");

        restTemplate.exchange("/api/v1/tasks", HttpMethod.POST, new HttpEntity<Task>(task), Task.class);
        restTemplate.exchange("/api/v1/tasks", HttpMethod.POST, new HttpEntity<Task>(task1), Task.class);
    }

    @Test
    void shouldReturnAllTasks() {
        Task[] tasks = restTemplate.getForObject("/api/v1/tasks", Task[].class);
        assertThat(tasks.length).isGreaterThan(2);
    }

    @Test
    void shouldReturnTaskWhenGivenValidId() {
        ResponseEntity<Task> response = restTemplate.exchange("/api/v1/tasks/id/1", HttpMethod.GET, null, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenGivenInvalidId() {
        ResponseEntity<Task> response = restTemplate.exchange("/api/v1/tasks/id/10", HttpMethod.GET, null, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnDoneTasks() {
        Task[] tasks = restTemplate.getForObject("/api/v1/tasks/Done", Task[].class);
        assertThat(tasks.length).isGreaterThan(0);
    }

    @Test
    void shouldReturnToDoTasks() {
        Task[] tasks = restTemplate.getForObject("/api/v1/tasks/ToDo", Task[].class);
        assertThat(tasks.length).isGreaterThan(1);
    }

    @Test
    void shouldReturnTasksWhenGivenOwner() {
        Task[] tasks = restTemplate.getForObject("/api/v1/tasks/owner/Fulano", Task[].class);
        assertThat(tasks.length).isGreaterThan(1);
    }

    @Test
    @Rollback
    void shouldCreateNewPostWhenPostIsValid() {
        Task task = new Task(3L, "Beltrano", "Treinar", "ToDo");

        ResponseEntity<Task> response = restTemplate.exchange("/api/v1/tasks", HttpMethod.POST, new HttpEntity<Task>(task), Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isEqualTo(3L);
        assertThat(response.getBody().getOwner()).isEqualTo("Beltrano");
        assertThat(response.getBody().getDescription()).isEqualTo("Treinar");
        assertThat(response.getBody().getCurrentstatus()).isEqualTo("ToDo");

    }

    @Test
    @Rollback
    void shouldUpdateTaskWhenIdIsValid() {
        ResponseEntity<Task> response = restTemplate.exchange("/api/v1/tasks/2", HttpMethod.PUT, null, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getOwner()).isEqualTo("Fulano");
        assertThat(response.getBody().getDescription()).isEqualTo("Estudar");
        assertThat(response.getBody().getCurrentstatus()).isEqualTo("Done");
    }

    @Test
    @Rollback
    void shouldDeleteTaskWhenGivenValidId() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/v1/tasks/2", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}