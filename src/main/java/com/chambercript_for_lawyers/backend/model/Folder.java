package com.chambercript_for_lawyers.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long caseId;   // Links to Case (can be null if it's a general folder)
    private Long clientId; // Links to Client

    // Self-referencing for endless sub-folders
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    @JsonIgnoreProperties({"subFolders", "hibernateLazyInitializer", "handler"})
    private Folder parentFolder;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("folder")
    private List<Document> documents;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("parentFolder")
    private List<Folder> subFolders;
}