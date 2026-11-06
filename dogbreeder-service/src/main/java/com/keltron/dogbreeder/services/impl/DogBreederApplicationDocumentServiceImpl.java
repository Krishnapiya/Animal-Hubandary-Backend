package com.keltron.dogbreeder.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.dogbreeder.dto.DogBreederApplicationDocumentDto;
import com.keltron.dogbreeder.entity.DogBreederApplicationDocument;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBreederApplicationDocumentRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.dogbreeder.repository.DocumentTypeRepository;
import com.keltron.utility.beans.dto.DocumentTypeDto;
import com.keltron.utility.jpa.entity.DocumentType;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;

import jakarta.annotation.PostConstruct;

@Service
public class DogBreederApplicationDocumentServiceImpl extends AbstractJpaService<
        DogBreederApplicationDocumentDto,
        Long,
        DogBreederApplicationDocumentRepository,
        DogBreederApplicationDocument> {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    @Value("${application.document.upload-dir:uploads}")
    private String uploadDir;

    private Path getUploadRootPath() {
        return Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    @PostConstruct
    public void initUploadFolder() {
        try {
            Files.createDirectories(getUploadRootPath());

            System.out.println("====================================");
            System.out.println("DOG BREEDER UPLOAD DIR VALUE: " + uploadDir);
            System.out.println("DOG BREEDER UPLOAD ROOT PATH: " + getUploadRootPath());
            System.out.println("UPLOAD FOLDER EXISTS: " + Files.exists(getUploadRootPath()));
            System.out.println("====================================");

        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<DocumentTypeDto> getDocumentTypes() {
        return documentTypeRepository.findByEntityScopeIgnoreCaseAndActiveTrue("DOG_BREEDER")
                .stream()
                .map(DocumentType::toDTO)
                .toList();
    }

    @Transactional
    public DogBreederApplicationDocument uploadDocument(
            Long applicationId,
            Long documentTypeId,
            MultipartFile file,
            Long uploadedBy
    ) throws IOException {

        System.out.println("========== DOG BREEDER DOCUMENT UPLOAD ==========");
        System.out.println("APPLICATION ID = " + applicationId);
        System.out.println("DOCUMENT TYPE ID = " + documentTypeId);
        System.out.println("UPLOADED BY = " + uploadedBy);
        System.out.println("FILE = " + (file != null ? file.getOriginalFilename() : null));
        System.out.println("UPLOAD DIR = " + uploadDir);
        System.out.println("UPLOAD ROOT = " + getUploadRootPath());
        System.out.println("=================================================");

        if (applicationId == null || applicationId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application ID missing");
        }

        if (documentTypeId == null || documentTypeId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document type ID missing");
        }

        if (uploadedBy == null || uploadedBy <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded by user ID missing");
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please choose a file");
        }

        long maxSize = 6 * 1024 * 1024; // 6 MB
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size cannot exceed 6 MB");
        }

        DogBreederRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found with id: " + applicationId));

        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document type not found with id: " + documentTypeId));

        if (documentType.getEntityScope() != null && !documentType.getEntityScope().equalsIgnoreCase("DOG_BREEDER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document type does not belong to DOG_BREEDER scope");
        }

        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String lowerName = originalFileName.toLowerCase();
        String contentType = file.getContentType();

        boolean isSignature = "APPLICANT_SIGNATURE".equalsIgnoreCase(documentType.getCode()) || documentTypeId == 21L;
        if (isSignature) {
            boolean isImage = (contentType != null && contentType.startsWith("image/"))
                    || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            if (!isImage) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please upload a JPG or PNG format signature");
            }
        } else {
            boolean isValidType = (contentType != null && (contentType.equals("application/pdf") || contentType.startsWith("image/")))
                    || lowerName.endsWith(".pdf") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            if (!isValidType) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supported file types: PDF, JPG, JPEG, PNG");
            }
        }

        Path uploadRoot = getUploadRootPath();
        Files.createDirectories(uploadRoot);

        Path applicationFolder = uploadRoot
                .resolve(String.valueOf(applicationId))
                .normalize();

        if (!applicationFolder.startsWith(uploadRoot)) {
            throw new RuntimeException("Invalid upload path");
        }

        Files.createDirectories(applicationFolder);

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = "document_" + documentTypeId + extension;

        Path targetPath = applicationFolder
                .resolve(storedFileName)
                .normalize();

        if (!targetPath.startsWith(applicationFolder)) {
            throw new RuntimeException("Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        DogBreederApplicationDocument document = repository
                .findTopByApplication_IdAndDocumentType_IdOrderByIdDesc(
                        applicationId,
                        documentTypeId
                )
                .orElse(new DogBreederApplicationDocument());

        document.setApplication(application);
        document.setDocumentType(documentType);

        Users user = new Users();
        user.setId(uploadedBy);
        document.setUploadedBy(user);

        String relativeFilePath = applicationId + "/" + storedFileName;

        document.setFileName(originalFileName);

        // Save only relative path in DB
        document.setFilePath(relativeFilePath);

        document.setMimeType(file.getContentType());
        document.setFileSizeBytes(file.getSize());
        document.setUploadedAt(LocalDateTime.now());

        DogBreederApplicationDocument savedDocument = repository.save(document);

        System.out.println("DOCUMENT SAVED ID = " + savedDocument.getId());
        System.out.println("DOCUMENT SAVED APPLICATION ID = " + applicationId);
        System.out.println("DB FILE PATH = " + relativeFilePath);
        System.out.println("PHYSICAL FILE PATH = " + targetPath.toAbsolutePath());

        return savedDocument;
    }

    @Transactional(readOnly = true)
    public List<DogBreederApplicationDocumentDto> findByApplicationId(Long applicationId) {
        return repository.findByApplication_IdOrderByIdAsc(applicationId)
                .stream()
                .map(DogBreederApplicationDocument::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DogBreederApplicationDocument getDocument(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @Transactional(readOnly = true)
    public Resource loadFile(Long id) {
        try {
            DogBreederApplicationDocument document = getDocument(id);

            if (document.getFilePath() == null || document.getFilePath().isBlank()) {
                throw new RuntimeException("File path missing");
            }

            Path savedPath = Paths.get(document.getFilePath()).normalize();
            Path resolvedPath;

            // Support old DB records with absolute path
            if (savedPath.isAbsolute()) {
                resolvedPath = savedPath;
            } else {
                resolvedPath = getUploadRootPath()
                        .resolve(savedPath)
                        .normalize();
            }

            System.out.println("========== DOG BREEDER DOCUMENT VIEW ==========");
            System.out.println("DOCUMENT ID = " + id);
            System.out.println("UPLOAD DIR = " + uploadDir);
            System.out.println("UPLOAD ROOT = " + getUploadRootPath());
            System.out.println("DB FILE PATH = " + document.getFilePath());
            System.out.println("RESOLVED FILE PATH = " + resolvedPath.toAbsolutePath());
            System.out.println("===============================================");

            Resource resource = new UrlResource(resolvedPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found: " + resolvedPath.toAbsolutePath());
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("File download failed", e);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {
        List<DogBreederApplicationDocumentDto> dtos = repository.findAll()
                .stream()
                .map(DogBreederApplicationDocument::toDTO)
                .toList();

        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }
}