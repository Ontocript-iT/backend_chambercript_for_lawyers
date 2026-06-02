package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

@Data
public class DocumentRequest {
    private String documentName;
    private String documentType;
    private String version;
    private String fileUrl;
    private String uploadedBy;
    private Long folderId;
}
