package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
