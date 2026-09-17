package com.keltron.citizen.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.citizen.dto.ComplaintInvestigationDto;
import com.keltron.citizen.entity.ComplaintInvestigation;
import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.repository.CitizenApplicationStatusMasterRepository;
import com.keltron.citizen.repository.ComplaintInvestigationRepository;
import com.keltron.citizen.repository.ComplaintRegistrationRepository;
import com.keltron.utility.constants.GrievanceApplicationStatus;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class ComplaintInvestigationServiceImpl
        extends AbstractJpaService<
                ComplaintInvestigationDto,
                Long,
                ComplaintInvestigationRepository,
                ComplaintInvestigation> {

    @Autowired
    private ComplaintRegistrationRepository complaintRepository;

    @Autowired
    private CitizenApplicationStatusMasterRepository statusRepository;

    @Autowired
    private ComplaintRegistrationStatusHistoryServiceImpl historyService;

    // =========================================================
    // SCHEDULE INVESTIGATION
    // =========================================================

    @Transactional
    public ComplaintInvestigationDto scheduleInvestigation(
            Long complaintId,
            LocalDate investigationDate,
            String investigationRemarks) {

        ComplaintRegistration complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ComplaintInvestigation investigation =
                new ComplaintInvestigation();

        investigation.setComplaint(complaint);

        investigation.setInvestigationDate(
                investigationDate);

        investigation.setInvestigationRemarks(
                investigationRemarks);

        ComplaintInvestigation savedInvestigation =
                repository.save(investigation);

        // Get current status before changing it
        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        ApplicationStatusMaster status =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus
                                .INVESTIGATION_SCHEDULED
                                .name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status INVESTIGATION_SCHEDULED not found"));

        complaint.setStatus(status);

        complaintRepository.save(complaint);

        // Save status history
        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                status.getStatusCode(),
                null,
                investigationRemarks,
                status.getStatusCode());

        return savedInvestigation.toDTO();
    }

    // =========================================================
    // UPLOAD INVESTIGATION REPORT
    // =========================================================

    @Transactional
    public ComplaintInvestigationDto uploadInvestigationReport(
            Long complaintId,
            MultipartFile reportFile,
            String remarks,
            String recommendation)
            throws IOException {

        // -----------------------------------------------------
        // 1. Find investigation using complaint ID
        // -----------------------------------------------------

        ComplaintInvestigation investigation =
                repository.findByComplaint_Id(complaintId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Investigation not found"));

        // -----------------------------------------------------
        // 2. Validate file
        // -----------------------------------------------------

        if (reportFile == null
                || reportFile.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Investigation report file is required");
        }

        // -----------------------------------------------------
        // 3. Create upload directory
        // -----------------------------------------------------

        Path uploadPath =
                Paths.get(
                        System.getProperty("user.home"),
                        "Documents",
                        "uploads",
                        "documents",
                        "complaints",
                        complaintId.toString(),
                        "investigation");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // -----------------------------------------------------
        // 4. Get original file name
        // -----------------------------------------------------

        String originalFileName =
                reportFile.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid report file name");
        }

        // Prevent path information from the client
        // from becoming part of the destination path.
        String fileName =
                Paths.get(originalFileName)
                        .getFileName()
                        .toString();

        // -----------------------------------------------------
        // 5. Physical file destination
        // -----------------------------------------------------

        Path destination =
                uploadPath.resolve(fileName);

        Files.copy(
                reportFile.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        // -----------------------------------------------------
        // 6. Save relative path in database
        // -----------------------------------------------------

        investigation.setInvestigationReportPath(
                "complaints/"
                        + complaintId
                        + "/investigation/"
                        + fileName);

        // -----------------------------------------------------
        // 7. Save report remarks
        // -----------------------------------------------------

        investigation.setReportRemarks(
                remarks);

        // -----------------------------------------------------
        // 8. Save recommendation
        // -----------------------------------------------------

        investigation.setRecommendation(
                recommendation);

        // -----------------------------------------------------
        // 9. Change complaint status based on recommendation
        // -----------------------------------------------------

        String statusCode;

        if ("VERIFY".equalsIgnoreCase(recommendation)) {

            statusCode =
                    GrievanceApplicationStatus
                            .VERIFIED_BY_CVO
                            .name();

        } else if ("REJECT".equalsIgnoreCase(recommendation)) {

            statusCode =
                    GrievanceApplicationStatus
                            .REJECTED_BY_CVO
                            .name();

        } else {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid recommendation. Expected VERIFY or REJECT");
        }

        ApplicationStatusMaster status =
                statusRepository.findByStatusCode(statusCode)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status " + statusCode + " not found"));

        ComplaintRegistration complaint =
                investigation.getComplaint();

        // Get current status before changing it
        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        complaint.setStatus(status);

        complaintRepository.save(complaint);

        // Save status history
        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                status.getStatusCode(),
                null,
                remarks,
                recommendation);

        // -----------------------------------------------------
        // 10. Save investigation
        // -----------------------------------------------------

        ComplaintInvestigation savedInvestigation =
                repository.save(investigation);

        return savedInvestigation.toDTO();
    }

    // =========================================================
    // VIEW
    // =========================================================

    @Transactional(readOnly = true)
    public ComplaintInvestigationDto getInvestigation(
            Long id) {

        ComplaintInvestigation investigation =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Investigation not found"));

        return investigation.toDTO();
    }

    // =========================================================
    // VIEW BY COMPLAINT
    // =========================================================

    @Transactional(readOnly = true)
    public ComplaintInvestigationDto getByComplaint(
            Long complaintId) {

        ComplaintInvestigation investigation =
                repository.findByComplaint_Id(complaintId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Investigation not found"));

        return investigation.toDTO();
    }

    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<ComplaintInvestigationDto> getInvestigations() {

        return repository.findAll()
                .stream()
                .map(ComplaintInvestigation::toDTO)
                .toList();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Transactional
    public Boolean deleteInvestigation(Long id) {

        ComplaintInvestigation investigation =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Investigation not found"));

        repository.delete(investigation);

        return true;
    }
}