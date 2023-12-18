package com.henrikolvr.todolist.repository;

import com.henrikolvr.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(Long id);
    List<Task> findByCurrentstatus(String currentstatus);
    List<Task> findByOwner(String owner);
}
