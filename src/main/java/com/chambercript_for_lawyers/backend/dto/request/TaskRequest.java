package com.chambercript_for_lawyers.backend.dto.request;


import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long assignedToId;
    private Long assignedById;
    private Long caseId;
}