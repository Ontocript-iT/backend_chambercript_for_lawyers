package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.TaskRequest;
import com.chambercript_for_lawyers.backend.model.Task;
import com.chambercript_for_lawyers.backend.services.central.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignTask(@RequestBody TaskRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Task savedTask = taskService.assignTask(request);
            response.put("status", 201);
            response.put("message", "Task assigned successfully");
            response.put("taskId", savedTask.getId());
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Failed to assign task: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getTasksForEmployee(@PathVariable Long employeeId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskService.getTasksForEmployee(employeeId);

            // Safe mapping to prevent Infinite Recursion
            List<Map<String, Object>> cleanTasks = tasks.stream().map(this::mapTaskToResponse).collect(Collectors.toList());

            response.put("status", 200);
            response.put("data", cleanTasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Error fetching tasks: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<?> getTasksByAdmin(@PathVariable Long adminId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskService.getTasksByAdmin(adminId);

            List<Map<String, Object>> cleanTasks = tasks.stream().map(this::mapTaskToResponse).collect(Collectors.toList());

            response.put("status", 200);
            response.put("data", cleanTasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Error fetching tasks: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam Long employeeId,
            @RequestParam String status) {

        HashMap<String, Object> response = new HashMap<>();
        try {
            taskService.updateTaskStatus(taskId, employeeId, status);
            response.put("status", 200);
            response.put("message", "Task status updated to " + status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", 400);
            response.put("message", "Failed to update status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private Map<String, Object> mapTaskToResponse(Task task) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("title", task.getTitle());
        map.put("description", task.getDescription());
        map.put("status", task.getStatus().toString());
        map.put("caseId", task.getCaseId());
        map.put("dueDate", task.getDueDate());
        map.put("assignedDate", task.getAssignedDate());

        if (task.getAssignedTo() != null) {
            map.put("assignedToName", task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName());
        }
        if (task.getAssignedBy() != null) {
            map.put("assignedByName", task.getAssignedBy().getFirstName()+ " " + task.getAssignedTo().getLastName());
        }
        return map;
    }
}