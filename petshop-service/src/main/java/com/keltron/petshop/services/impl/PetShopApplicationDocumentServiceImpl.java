package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

@Service
public class PetShopApplicationDocumentServiceImpl
        extends AbstractJpaService<
                PetShopApplicationDocumentDto,
                Long,
                PetShopApplicationDocumentRepository,
                PetShopApplicationDocument> {

    // =========================================================
    // Excel Export
    // =========================================================

    public ByteArrayOutputStream generateExcel(
            ExcelExportRequest request) {

        List<PetShopApplicationDocumentDto> dtos =
                repository.findAll()
                        .stream()
                        .map(PetShopApplicationDocument::toDTO)
                        .toList();

        return ExcelExportUtil.generateExcel(
                dtos,
                request.getXls_config());
    }

    // =========================================================
    // Draft Documents
    // =========================================================

    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = true)
    public List<PetShopApplicationDocumentDto> getDraft(
            Long applicationId) {

        return repository
                .findByApplication_Id(applicationId)
                .stream()
                .map(PetShopApplicationDocument::toDTO)
                .toList();
    }

    // =========================================================
    // Upload
    // =========================================================

    public PetShopApplicationDocumentDto uploadDocument(
            MultipartFile file,
            Long applicationId,
            Long documentTypeId,
            Long uploadedBy)
            throws IOException {

        Path uploadPath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads",
                "documents",
                applicationId.toString());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName =
                file.getOriginalFilename();

        Path destination =
                uploadPath.resolve(originalFileName);

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        PetShopApplicationDocumentDto dto =
                new PetShopApplicationDocumentDto();

        dto.setApplicationId(applicationId);
        dto.setDocumentTypeId(documentTypeId);
        dto.setFileName(originalFileName);
        dto.setFilePath(
                applicationId + "/" + originalFileName);
        dto.setMimeType(file.getContentType());
        dto.setFileSizeBytes(file.getSize());
        dto.setUploadedBy(uploadedBy);
        dto.setUploadedAt(LocalDateTime.now());

        PetShopApplicationDocument savedDocument =
                save(dto);

        return savedDocument.toDTO();
    }

    // =========================================================
    // View
    // =========================================================

    public ResponseEntity<Resource> viewDocument(
            String fileName)
            throws IOException {

        Path filePath =
                Paths.get(
                        System.getProperty("user.home"),
                        "Documents",
                        "uploads",
                        "documents")
                        .resolve(fileName);

        System.out.println("==============================");
        System.out.println("Received : " + fileName);
        System.out.println("Searching: " + filePath);
        System.out.println("Exists   : " + Files.exists(filePath));
        System.out.println("==============================");

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        String contentType = Files.probeContentType(filePath);

        if (contentType == null || contentType.isBlank()) {
            String lowerName = fileName.toLowerCase();

            if (lowerName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerName.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerName.endsWith(".jpg")
                    || lowerName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else {
                contentType = "application/octet-stream";
            }
        }

        String displayName = fileName.contains("/")
                ? fileName.substring(fileName.lastIndexOf('/') + 1)
                : fileName;

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(displayName)
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}