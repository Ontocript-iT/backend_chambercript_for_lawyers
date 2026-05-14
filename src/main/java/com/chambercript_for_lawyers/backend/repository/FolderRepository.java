package com.chambercript_for_lawyers.backend.repository;


import com.chambercript_for_lawyers.backend.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByParentFolderIsNullAndCaseId(Long caseId); // Get root folders of a case
    List<Folder> findByParentFolderId(Long parentFolderId);
}