package com.chambercript_for_lawyers.backend.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderRequest {

    private String documentName;
    private String documentType;
    private String version;
    private String fileUrl;
    private String uploadedBy;
    private Long folderId;
    private String name;
    private Long caseId;
    private Long clientId;
    private Long parentFolderId;
}
