package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long employeeId);
    List<Task> findByAssignedById(Long adminId);
}