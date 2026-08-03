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
		        inspection.getInspectionDate()
		                  .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

		String message =
		        "Your inspection has been scheduled by the Chief Veterinary Officer.\n\n"
		      + "Inspection Date : " + inspectionDate;

		if (inspection.getInspectionRemarks() != null
		        && !inspection.getInspectionRemarks().isBlank()) {

		    message += "\nRemarks : " + inspection.getInspectionRemarks();
		}

		notificationService.createNotification(
		        application.getApplicantUserId(),
		        "PET_SHOP",
		        application.getId(),
		        "Inspection Scheduled",
		        message,
		        "INFO");
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

		return repository.findByApplication_Id(applicationId).orElse(null);
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

	public RegistrationInspection uploadInspectionReport(Long applicationId, MultipartFile reportFile, String remarks,
			String recommendation) {

		RegistrationInspection inspection = repository.findByApplication_Id(applicationId)
				.orElseThrow(() -> new RuntimeException("Inspection not found."));

		try {

			Path reportDirectory = Paths.get(inspectionUploadDir);

			Files.createDirectories(reportDirectory);

			String fileName = System.currentTimeMillis() + "_" + reportFile.getOriginalFilename();

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
}