package com.chambercript_for_lawyers.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentName;
    private String documentType; // Plaint, Evidence, etc.
    private String version;
    private String fileUrl; // S3 or local path link
    private String uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonIgnoreProperties({"documents", "subFolders", "hibernateLazyInitializer", "handler"})
    private Folder folder;
}
