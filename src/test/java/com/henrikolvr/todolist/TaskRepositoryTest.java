package com.henrikolvr.todolist;

import com.henrikolvr.todolist.config.ContainersEnvironment;
import com.henrikolvr.todolist.model.Task;
import com.henrikolvr.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskRepositoryTest extends ContainersEnvironment {

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {

        Task task = new Task(1L, "Fulano", "Trabalhar", "Done");
        Task task1 = new Task(2l, "Ciclano", "Estudar", "Done");
        Task task2 = new Task(3L, "Beltrano", "Treinar", "ToDo");
        Task task3 = new Task(4L, "Beltrano", "Treinar", "Done");
        Task task4 = new Task(5L, "Ciclano", "Treinar", "ToDo");

        taskRepository.save(task);
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
    }

    @Test
    void ShouldReturnAListOfTasks() {
        List<Task> list = taskRepository.findAll();
        assertEquals(5, list.size());
    }

    @Test
    void ShouldReturnAListOfTasksByCurrentStatusToDo() {
        List<Task> list = taskRepository.findByCurrentstatus("ToDo");

        assertEquals(2, list.size());
    }

    @Test
    void ShouldReturnAListOfTasksByCurrentStatusDone() {
        List<Task> list = taskRepository.findByCurrentstatus("Done");

        assertEquals(3, list.size());
    }

    @Test
    void ShouldReturnAListOfTasksByOwner() {
        List<Task> list = taskRepository.findByOwner("Ciclano");
        assertEquals(2, list.size());
    }

    @Test
    void ShouldReturnAnTaskById() {
        Optional<Task> list = taskRepository.findById(2L);

        assertEquals(2, list.get().getId());
    }
}
