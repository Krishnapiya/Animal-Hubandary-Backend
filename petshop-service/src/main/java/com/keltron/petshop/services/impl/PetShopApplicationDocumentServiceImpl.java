package com.keltron.petshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.entity.PetShopApplicationDocument;
import com.keltron.petshop.repository.DocumentTypeRepository;
import com.keltron.petshop.repository.PetShopApplicationDocumentRepository;
import com.keltron.petshop.repository.RegistrationApplicationRepository;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.dto.DocumentTypeDto;
import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.entity.RegistrationApplication;
import com.keltron.utility.jpa.entity.Users;
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

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private RegistrationApplicationRepository applicationRepository;

    // =========================================================
    // Document Types for Pet Shop
    // =========================================================

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<DocumentTypeDto> getDocumentTypes() {
        return documentTypeRepository.findByEntityScopeIgnoreCaseAndActiveTrue("PET_SHOP")
                .stream()
                .map(DocumentType::toDTO)
                .toList();
    }

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

    @Transactional
    public PetShopApplicationDocumentDto uploadDocument(
            MultipartFile file,
            Long applicationId,
            Long documentTypeId,
            Long uploadedBy)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        long maxSize = 6 * 1024 * 1024; // 6 MB
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size cannot exceed 6 MB");
        }

        RegistrationApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found with id: " + applicationId));

        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document type not found with id: " + documentTypeId));

        if (documentType.getEntityScope() != null && !documentType.getEntityScope().equalsIgnoreCase("PET_SHOP")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document type does not belong to PET_SHOP scope");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "document_" + documentTypeId;
        }

        String lowerName = originalFileName.toLowerCase();
        String contentType = file.getContentType();

        boolean isSignature = "APPLICANT_SIGNATURE".equalsIgnoreCase(documentType.getCode()) || documentTypeId == 9L;
        if (isSignature) {
            boolean isImage = (contentType != null && contentType.startsWith("image/"))
                    || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            if (!isImage) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please upload a JPG or PNG signature");
            }
        } else {
            boolean isValidType = (contentType != null && (contentType.equals("application/pdf") || contentType.startsWith("image/")))
                    || lowerName.endsWith(".pdf") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            if (!isValidType) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supported file types: PDF, JPG, JPEG, PNG");
            }
        }

        Path uploadPath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "uploads",
                "documents",
                applicationId.toString());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destination = uploadPath.resolve(originalFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Avoid duplicate PAN Card or any document type entry in the database
        Optional<PetShopApplicationDocument> existingDocOpt = repository
                .findByApplication_Id(applicationId)
                .stream()
                .filter(d -> d.getDocumentType() != null && d.getDocumentType().getId().equals(documentTypeId))
                .findFirst();

        PetShopApplicationDocument entityToSave;
        if (existingDocOpt.isPresent()) {
            entityToSave = existingDocOpt.get();
        } else {
            entityToSave = new PetShopApplicationDocument();
            entityToSave.setApplication(app);
            entityToSave.setDocumentType(documentType);
        }

        entityToSave.setFileName(originalFileName);
        entityToSave.setFilePath(applicationId + "/" + originalFileName);
        entityToSave.setMimeType(file.getContentType());
        entityToSave.setFileSizeBytes(file.getSize());
        if (ValidationUtils.isValid(uploadedBy)) {
            entityToSave.setUploadedBy(new Users(uploadedBy));
        }
        entityToSave.setUploadedAt(LocalDateTime.now());

        PetShopApplicationDocument savedDocument = repository.save(entityToSave);
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

        if (!Files.exists(filePath)) {
            String decodedName = fileName;
            try {
                decodedName = java.net.URLDecoder.decode(fileName, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            java.util.List<Path> candidates = java.util.List.of(
                Paths.get(System.getProperty("user.home"), "Documents", "uploads", "Reports").resolve(fileName),
                Paths.get(System.getProperty("user.home"), "Documents", "uploads", "Reports").resolve(decodedName),
                Paths.get("/home/user/Documents/uploads/Reports").resolve(fileName),
                Paths.get("/home/user/Documents/uploads/Reports").resolve(decodedName),
                Paths.get("/home/user/Documents/uploads/documents").resolve(fileName),
                Paths.get("/home/user/Documents/uploads/documents").resolve(decodedName),
                Paths.get("/home/keltron/Documents/uploads/Reports").resolve(fileName),
                Paths.get("/home/keltron/Documents/uploads/Reports").resolve(decodedName)
            );

            for (Path candidate : candidates) {
                if (Files.exists(candidate)) {
                    filePath = candidate;
                    break;
                }
            }

            if (!Files.exists(filePath)) {
                if (Paths.get(fileName).isAbsolute() && Files.exists(Paths.get(fileName))) {
                    filePath = Paths.get(fileName);
                } else if (Paths.get(decodedName).isAbsolute() && Files.exists(Paths.get(decodedName))) {
                    filePath = Paths.get(decodedName);
                }
            }
        }

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
