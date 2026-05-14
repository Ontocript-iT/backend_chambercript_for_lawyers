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
    private String documentType;
    private String version;
    private String fileUrl;
    private String uploadedBy;
    private String lawFirmCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonIgnoreProperties({"documents", "subFolders", "hibernateLazyInitializer", "handler"})
    private Folder folder;
}
