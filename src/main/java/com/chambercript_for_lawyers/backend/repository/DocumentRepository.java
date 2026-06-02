package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByFolderId(Long folderId);
}