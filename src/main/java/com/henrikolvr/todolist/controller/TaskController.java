package com.henrikolvr.todolist.controller;

import com.henrikolvr.todolist.exception.TaskNotFoundException;
import com.henrikolvr.todolist.model.Task;
import com.henrikolvr.todolist.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Task> findAll() {
        return taskRepository.findAll();
    }

    @GetMapping("/Done")
    @ResponseStatus(HttpStatus.OK)
    List<Task> findByCurrentstatusDone() {
        return taskRepository.findByCurrentstatus("Done");
    }

    @GetMapping("/ToDo")
    @ResponseStatus(HttpStatus.OK)
    List<Task> findByCurrentstatusToDo() {
        return taskRepository.findByCurrentstatus("ToDo");
    }

    @GetMapping("/owner/{owner}")
    @ResponseStatus(HttpStatus.OK)
    List<Task> findByOwner(@PathVariable String owner) {
        return taskRepository.findByOwner(owner);
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    Optional<Task> findById(@PathVariable Long id) {
        return Optional.ofNullable(taskRepository.findById(id).orElseThrow(TaskNotFoundException::new));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Task update(@PathVariable Long id) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()) {
            Task updatedTask = new Task(existingTask.get().getId(),
                    existingTask.get().getOwner(),
                    existingTask.get().getDescription(),
                    "Done");

            return taskRepository.save(updatedTask);
        } else {
            throw new TaskNotFoundException();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Task save(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
