package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.TaskRequest;
import com.chambercript_for_lawyers.backend.model.Task;

import java.util.List;

public interface TaskService {
    Task assignTask(TaskRequest request);
    List<Task> getTasksForEmployee(Long employeeId);
    List<Task> getTasksByAdmin(Long adminId);
    Task updateTaskStatus(Long taskId, Long employeeId, String newStatus);
}