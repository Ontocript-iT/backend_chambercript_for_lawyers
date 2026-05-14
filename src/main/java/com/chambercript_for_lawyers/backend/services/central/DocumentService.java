package com.chambercript_for_lawyers.backend.services.central;


import com.chambercript_for_lawyers.backend.dto.request.DocumentRequest;
import com.chambercript_for_lawyers.backend.model.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {

    ResponseEntity<?> uploadDocument(MultipartFile file, String documentType, String version, String uploadedBy, Long folderId,String lawFirmCode);
}
