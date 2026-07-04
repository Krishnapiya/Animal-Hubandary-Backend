//package com.keltron.admin.services.impl;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import com.keltron.admin.repository.ApplicationDocumentRepository;
//import com.keltron.utility.beans.dto.ApplicationDocumentDto;
//import com.keltron.utility.jpa.entity.ApplicationDocument;
//import com.keltron.utility.manage.service.abs.AbstractJpaService;
//import com.keltron.utility.manage.service.abs.ExcelExportUtil;
//import com.keltron.utility.requests.ExcelExportRequest;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.transaction.annotation.Propagation;
//import java.io.IOException;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.time.LocalDateTime;
//
//import org.springframework.web.multipart.MultipartFile;
//@Service
//public class ApplicationDocumentServiceImpl
//        extends AbstractJpaService<ApplicationDocumentDto, Long,
//        ApplicationDocumentRepository, ApplicationDocument> {
//
//	public ByteArrayOutputStream generateExcel(
//            ExcelExportRequest request) {
//
//        List<ApplicationDocumentDto> dtos =
//                repository.findAll()
//                        .stream()
//                        .map(ApplicationDocument::toDTO)
//                        .toList();
//
//        return ExcelExportUtil.generateExcel(
//                dtos,
//                request.getXls_config());
//}
//	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
//	public List<ApplicationDocumentDto> getDraft(Long applicationId) {
//
//	    return repository
//	            .findByApplicationId(applicationId)
//	            .stream()
//	            .map(ApplicationDocument::toDTO)
//	            .toList();
//	}
//	public ApplicationDocumentDto uploadDocument(
//	        MultipartFile file,
//	        Long applicationId,
//	        Long documentTypeId,
//	        Long uploadedBy)
//	        throws IOException {
//		
//		// Create one folder for each application
//		Path uploadPath = Paths.get(
//		        System.getProperty("user.home"),
//		        "Documents",
//		        "uploads",
//		        "documents",
//		        applicationId.toString());
//
//		// Create the folder if it doesn't exist
//		if (!Files.exists(uploadPath)) {
//		    Files.createDirectories(uploadPath);
//		}
//	    // Original filename
//	    String originalFileName = file.getOriginalFilename();
//
//	    String savedFileName = originalFileName;
//	    
//
//	    // Destination
//	    Path destination =
//	            uploadPath.resolve(savedFileName);
//
//	    // Copy file
//	    Files.copy(
//	            file.getInputStream(),
//	            destination,
//	            StandardCopyOption.REPLACE_EXISTING);
//
//	    System.out.println("Saved File : " + destination.toAbsolutePath());
//
//	    ApplicationDocumentDto dto = new ApplicationDocumentDto();
//
//	    dto.setApplicationId(applicationId);
//	    dto.setDocumentTypeId(documentTypeId);
//
//	    dto.setFileName(originalFileName);
//
//	    // Save only the generated filename
//	 // Save relative path
//	    dto.setFilePath(applicationId + "/" + savedFileName);
//	    dto.setMimeType(file.getContentType());
//
//	    dto.setFileSizeBytes(file.getSize());
//
//	    dto.setUploadedBy(uploadedBy);
//
//	    dto.setUploadedAt(LocalDateTime.now());
//	    System.out.println("============ DTO ============");
//	    System.out.println("File Name : " + dto.getFileName());
//	    System.out.println("File Path : " + dto.getFilePath());
//	    System.out.println("=============================");
//	    ApplicationDocument savedDocument = save(dto);
//
//	    return savedDocument.toDTO();
//	}
//	public ResponseEntity<Resource> viewDocument(String fileName)
//	        throws IOException {
//
//		Path filePath = Paths.get(
//		        System.getProperty("user.home"),
//		        "Documents",
//		        "uploads",
//		        "documents")
//		        .resolve(fileName);
//
//	    Resource resource = new UrlResource(filePath.toUri());
//
//	    if (!resource.exists()) {
//	        throw new RuntimeException("File not found");
//	    }
//
//	    String contentType = Files.probeContentType(filePath);
//
//	    return ResponseEntity.ok()
//	            .contentType(MediaType.parseMediaType(
//	                    contentType != null
//	                            ? contentType
//	                            : "application/octet-stream"))
//	            .body(resource);
//	}
//    }
//	
