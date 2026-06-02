package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.TaskRequest;
import com.chambercript_for_lawyers.backend.enums.TaskStatus;
import com.chambercript_for_lawyers.backend.model.Task;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.TaskRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Task assignTask(TaskRequest request) {
        User admin = userRepository.findById(request.getAssignedById())
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + request.getAssignedById()));

        User employee = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getAssignedToId()));



        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedBy(admin);
        task.setAssignedTo(employee);
        task.setCaseId(request.getCaseId());

        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasksForEmployee(Long employeeId) {
        return taskRepository.findByAssignedToId(employeeId);
    }

    @Override
    public List<Task> getTasksByAdmin(Long adminId) {
        return taskRepository.findByAssignedById(adminId);
    }

    @Override
    @Transactional
    public Task updateTaskStatus(Long taskId, Long employeeId, String newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedTo().getId().equals(employeeId)) {
            throw new RuntimeException("You are not authorized to update this task.");
        }

        try {
            task.setStatus(TaskStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Allowed values: PENDING, IN_PROGRESS, COMPLETED");
        }

        return taskRepository.save(task);
    }
}