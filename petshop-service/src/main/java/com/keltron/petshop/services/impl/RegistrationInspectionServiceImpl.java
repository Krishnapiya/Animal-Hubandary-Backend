package com.keltron.petshop.services.impl;

import java.util.List;
import java.util.Optional;
import com.keltron.petshop.repository.ApplicationStatusMasterRepository;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.petshop.dto.RegistrationInspectionDto;
import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.entity.RegistrationInspection;
import com.keltron.petshop.repository.PetShopRegistrationApplicationRepository;
import com.keltron.petshop.repository.RegistrationInspectionRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.multipart.MultipartFile;
import com.keltron.utility.constants.ApplicationStatus;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
@Service
@Transactional
public class RegistrationInspectionServiceImpl {

	@Autowired
	private RegistrationInspectionRepository repository;

	@Autowired
	private PetShopRegistrationApplicationRepository applicationRepository;

	@Autowired
	private ApplicationStatusMasterRepository applicationStatusMasterRepository;
	@Autowired
	private PetShopNotificationServiceImpl notificationService;

	@Autowired
	private RegistrationApplicationStatusHistoryServiceImpl historyService;

	@Value("${application.inspection.upload-dir}")
	private String inspectionUploadDir;

	/**
	 * Save Inspection Schedule
	 */
	public RegistrationInspection save(RegistrationInspectionDto dto) {

		RegistrationInspection inspection = new RegistrationInspection();

		inspection.copyFromDTO(dto);

		PetShopRegistrationApplication application = applicationRepository.findById(dto.getApplicationId())
				.orElseThrow(() -> new RuntimeException("Application not found."));

		ApplicationStatus fromStatus = application.getStatus() == null ? null
				: ApplicationStatus.valueOf(application.getStatus().getStatusCode());

		inspection.setApplication(application);

		RegistrationInspection savedInspection = repository.save(inspection);

		ApplicationStatusMaster inspectionScheduled = applicationStatusMasterRepository
				.findByStatusCode(ApplicationStatus.INSPECTION_SCHEDULED.name())
				.orElseThrow(() -> new RuntimeException("Status INSPECTION_SCHEDULED not found."));

		application.setStatus(inspectionScheduled);

		applicationRepository.save(application);
		historyService.logStatusChange(application.getId(), fromStatus, ApplicationStatus.INSPECTION_SCHEDULED,
				"SYSTEM", "Inspection scheduled", "SCHEDULE_INSPECTION");

		String inspectionDate =
		        inspection.getInspectionDate() != null
		                ? inspection.getInspectionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
		                : "";

		boolean isVisible = Boolean.TRUE.equals(savedInspection.getInspectionDateVisible());

		String applicantMessage;
		if (isVisible) {
			applicantMessage =
			        "Your inspection has been scheduled by the Chief Veterinary Officer.\n\n"
			      + "Inspection Date : " + inspectionDate;
			if (inspection.getInspectionRemarks() != null
			        && !inspection.getInspectionRemarks().isBlank()) {
			    applicantMessage += "\nRemarks : " + inspection.getInspectionRemarks();
			}
		} else {
			applicantMessage =
			        "Your inspection has been scheduled by the Chief Veterinary Officer. Details will be made available by the department.";
		}

		if (application.getApplicantUserId() != null) {
			notificationService.createNotification(
			        application.getApplicantUserId(),
			        "PET_SHOP",
			        application.getId(),
			        "Inspection Scheduled",
			        applicantMessage,
			        "INFO");
		}

		if (!isVisible) {
			try {
				Long cvoUserId = notificationService.getLoggedInUserId();
				if (cvoUserId != null) {
					String appNum = application.getApplicationNumber() != null ? application.getApplicationNumber() : "";
					notificationService.createNotification(
					        cvoUserId,
					        "PET_SHOP",
					        application.getId(),
					        "Inspection Scheduled (Surprise Visit)",
					        "Inspection for application " + appNum + " has been scheduled for " + inspectionDate + " (hidden from applicant).",
					        "INFO");
				}
			} catch (Exception ex) {
				// Ignore if security context has no user
			}
		}

		return savedInspection;
	}

	/**
	 * Update Inspection
	 */
	public RegistrationInspection update(Long id, RegistrationInspectionDto dto) {

		RegistrationInspection inspection = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Inspection not found."));

		inspection.copyFromDTO(dto);

		if (dto.getApplicationId() != null) {

			PetShopRegistrationApplication application = applicationRepository.findById(dto.getApplicationId())
					.orElseThrow(() -> new RuntimeException("Application not found."));

			inspection.setApplication(application);
		}

		return repository.save(inspection);
	}

	/**
	 * Get Inspection By Id
	 */
	@Transactional(readOnly = true)
	public RegistrationInspection get(Long id) {

		return repository.findById(id).orElseThrow(() -> new RuntimeException("Inspection not found."));
	}

	/**
	 * Get Inspection By Application
	 */
	@Transactional(readOnly = true)
	public RegistrationInspection getByApplication(Long applicationId) {

		return repository.findFirstByApplication_IdOrderByIdDesc(applicationId).orElse(null);
	}

	/**
	 * List All Inspections
	 */
	@Transactional(readOnly = true)
	public List<RegistrationInspection> getAll() {

		return repository.findAllByOrderByIdDesc();
	}

	/**
	 * Delete Inspection
	 */
	public boolean delete(Long id) {

		RegistrationInspection inspection = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Inspection not found."));

		repository.delete(inspection);

		return true;
	}

	/**
	 * Toggle inspection date visibility for applicant
	 */
	public RegistrationInspection toggleVisibility(Long applicationId, Boolean visible) {
		RegistrationInspection inspection = repository
				.findFirstByApplication_IdOrderByIdDesc(applicationId)
				.orElseThrow(() -> new RuntimeException("Inspection not found for application: " + applicationId));

		boolean newVisibility = (visible != null) ? visible : !Boolean.TRUE.equals(inspection.getInspectionDateVisible());
		inspection.setInspectionDateVisible(newVisibility);
		RegistrationInspection saved = repository.save(inspection);

		if (newVisibility) {
			PetShopRegistrationApplication app = inspection.getApplication();
			if (app != null && app.getApplicantUserId() != null) {
				String dateStr = inspection.getInspectionDate() != null
						? inspection.getInspectionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
						: "";
				String appNum = app.getApplicationNumber() != null ? app.getApplicationNumber() : "";
				notificationService.createNotification(
						app.getApplicantUserId(),
						"PET_SHOP",
						app.getId(),
						"Inspection Date Revealed",
						"The inspection date for your application (" + appNum + ") is scheduled on " + dateStr + ".",
						"INFO");
			}
		}

		return saved;
	}

	public RegistrationInspection uploadInspectionReport(Long applicationId, MultipartFile reportFile, String remarks,
			String recommendation) {

		RegistrationInspection inspection = repository
				.findFirstByApplication_IdOrderByIdDesc(applicationId)
				.orElseThrow(() -> new RuntimeException("Inspection not found."));

		try {

			Path reportDirectory;
			if (inspectionUploadDir != null && !inspectionUploadDir.isBlank() && !inspectionUploadDir.contains("/home/keltron")) {
				reportDirectory = Paths.get(inspectionUploadDir);
			} else {
				reportDirectory = Paths.get(System.getProperty("user.home"), "Documents", "uploads", "Reports");
			}

			if (!Files.exists(reportDirectory)) {
				Files.createDirectories(reportDirectory);
			}

			String rawName = reportFile.getOriginalFilename() != null ? reportFile.getOriginalFilename() : "report.pdf";
			String cleanName = Paths.get(rawName).getFileName().toString();
			String fileName = System.currentTimeMillis() + "_" + cleanName;

			Path filePath = reportDirectory.resolve(fileName);

			Files.copy(reportFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			inspection.setInspectionReport(filePath.toString());

		} catch (IOException e) {

			throw new RuntimeException("Failed to upload inspection report.", e);
		}

		inspection.setInspectionRemarks(remarks);

		inspection.setRecommendation(recommendation);

//        inspection.setStatus(recommendation);

		PetShopRegistrationApplication application = inspection.getApplication();

		ApplicationStatus fromStatus = application.getStatus() == null ? null
				: ApplicationStatus.valueOf(application.getStatus().getStatusCode());

		String statusCode = recommendation.equalsIgnoreCase("APPROVED") ? ApplicationStatus.VERIFIED_BY_CVO.name()
				: ApplicationStatus.REJECTED_BY_CVO.name();

		ApplicationStatusMaster applicationStatus = applicationStatusMasterRepository.findByStatusCode(statusCode)
				.orElseThrow(() -> new RuntimeException("Status not found : " + statusCode));

		application.setStatus(applicationStatus);

		applicationRepository.save(application);
		historyService.logStatusChange(application.getId(), fromStatus, ApplicationStatus.valueOf(statusCode), "SYSTEM",
				recommendation.equalsIgnoreCase("APPROVED") ? "Application verified by CVO"
						: "Application rejected by CVO",
				recommendation.equalsIgnoreCase("APPROVED") ? "VERIFY" : "REJECT");

		return repository.save(inspection);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<Resource> viewReport(Long applicationId) throws IOException {
		RegistrationInspection inspection = repository
				.findFirstByApplication_IdOrderByIdDesc(applicationId)
				.orElseThrow(() -> new RuntimeException("Inspection not found."));

		String reportPath = inspection.getInspectionReport();
		if (reportPath == null || reportPath.isBlank()) {
			throw new RuntimeException("Inspection report not found.");
		}

		Path path = Paths.get(reportPath);
		if (!Files.exists(path)) {
			String fileName = path.getFileName().toString();
			String decodedName = fileName;
			try {
				decodedName = java.net.URLDecoder.decode(fileName, java.nio.charset.StandardCharsets.UTF_8);
			} catch (Exception ignored) {}

			java.util.List<Path> candidates = java.util.List.of(
				inspectionUploadDir != null && !inspectionUploadDir.isBlank() ? Paths.get(inspectionUploadDir).resolve(fileName) : path,
				inspectionUploadDir != null && !inspectionUploadDir.isBlank() ? Paths.get(inspectionUploadDir).resolve(decodedName) : path,
				Paths.get("/home/user/Documents/uploads/Reports").resolve(fileName),
				Paths.get("/home/user/Documents/uploads/Reports").resolve(decodedName),
				Paths.get(System.getProperty("user.home"), "Documents", "uploads", "Reports").resolve(fileName),
				Paths.get(System.getProperty("user.home"), "Documents", "uploads", "Reports").resolve(decodedName),
				Paths.get("/home/user/Documents/uploads/documents").resolve(fileName),
				Paths.get("/home/user/Documents/uploads/documents").resolve(decodedName),
				Paths.get("/home/keltron/Documents/uploads/Reports").resolve(fileName),
				Paths.get("/home/keltron/Documents/uploads/Reports").resolve(decodedName)
			);

			for (Path candidate : candidates) {
				if (Files.exists(candidate)) {
					path = candidate;
					break;
				}
			}
		}

		if (!Files.exists(path)) {
			throw new RuntimeException("Report file does not exist on server: " + reportPath);
		}

		Resource resource = new UrlResource(path.toUri());
		String contentType = Files.probeContentType(path);
		if (contentType == null || contentType.isBlank()) {
			String lower = path.getFileName().toString().toLowerCase();
			if (lower.endsWith(".pdf")) {
				contentType = "application/pdf";
			} else if (lower.endsWith(".png")) {
				contentType = "image/png";
			} else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
				contentType = "image/jpeg";
			} else {
				contentType = "application/octet-stream";
			}
		}

		String displayName = path.getFileName().toString();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.inline().filename(displayName).build().toString())
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource);
	}
}