package com.keltron.citizen.services.impl;

import java.util.List;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.citizen.dto.ComplaintDocumentDto;
import com.keltron.citizen.dto.ComplaintRegistrationDto;
import com.keltron.citizen.dto.ComplaintRegistrationViewDto;
import com.keltron.citizen.entity.ComplaintDocument;
import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.repository.CitizenApplicationStatusMasterRepository;
import com.keltron.citizen.repository.ComplaintDocumentRepository;
import com.keltron.citizen.repository.ComplaintRegistrationRepository;
import com.keltron.utility.constants.GrievanceApplicationStatus;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
@Service
public class ComplaintRegistrationServiceImpl
        extends AbstractJpaService<
                ComplaintRegistrationDto,
                Long,
                ComplaintRegistrationRepository,
                ComplaintRegistration> {

    @Autowired
    private CitizenApplicationStatusMasterRepository statusRepository;
    
    @Autowired
    private ComplaintDocumentRepository complaintDocumentRepository;
    
    @Autowired
    private ComplaintRegistrationStatusHistoryServiceImpl historyService;

    @Override
    @Transactional
    public ComplaintRegistration save(ComplaintRegistrationDto dto) {

        dto.setCitizenUserId(1L); // temporary

        if (dto.getComplaintNumber() == null
                || dto.getComplaintNumber().isBlank()) {

            dto.setComplaintNumber(
                    "CMP" + System.currentTimeMillis());
        }

        ApplicationStatusMaster status;

        if (dto.getStatusId() == null) {

            status = statusRepository
                    .findByStatusCode(
                            GrievanceApplicationStatus.SUBMITTED.name())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "SUBMITTED status not found"));

            dto.setStatusId(status.getId());

        } else {

            status = statusRepository
                    .findById(dto.getStatusId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Status not found"));
        }

        ComplaintRegistration complaint =
                super.save(dto);

        historyService.logStatusChange(
                complaint.getId(),
                null,
                status.getStatusCode(),
                null,
                null,
                status.getStatusCode());

        return complaint;
    }

    @Transactional(readOnly = true)
    public List<ComplaintRegistrationDto> getComplaints() {

        return repository.findAll()
                .stream()
                .map(ComplaintRegistration::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintRegistrationViewDto getComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ComplaintRegistrationViewDto dto =
                new ComplaintRegistrationViewDto();

        // Complaint details
        dto.setId(complaint.getId());
        dto.setComplaintNumber(complaint.getComplaintNumber());
        dto.setCitizenUserId(complaint.getCitizenUserId());
        dto.setPlaceOfIncident(complaint.getPlaceOfIncident());
        dto.setPetAnimalName(complaint.getPetAnimalName());
        dto.setComplaintDescription(
                complaint.getComplaintDescription());
        dto.setIncidentDate(complaint.getIncidentDate());

        // Existing attachment paths
        dto.setPhotoPath(complaint.getPhotoPath());
        dto.setVideoPath(complaint.getVideoPath());
        dto.setDocumentPath(complaint.getDocumentPath());

        // Status
        if (complaint.getStatus() != null) {

            dto.setStatusId(
                    complaint.getStatus().getId());

            dto.setStatus(
                    complaint.getStatus()
                            .toDropDownPayload()
                            .getName());
        }

        // Supporting documents
        List<ComplaintDocumentDto> documents =
                complaintDocumentRepository
                        .findByComplaint_Id(id)
                        .stream()
                        .map(ComplaintDocument::toDTO)
                        .toList();

        dto.setSupportingDocuments(documents);

        return dto;
    }

    @Transactional
    public Boolean deleteComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        repository.delete(complaint);

        return true;
    }
    @Transactional
    public ComplaintRegistrationDto forwardComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        ApplicationStatusMaster toStatus =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus.FORWARDED_TO_CVO.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status FORWARDED_TO_CVO not found"));

        complaint.setStatus(toStatus);

        repository.save(complaint);

        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                toStatus.getStatusCode(),
                null,
                null,
                toStatus.getStatusCode());

        return complaint.toDTO();
    }
    @Transactional
    public ComplaintRegistrationDto startReview(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        ApplicationStatusMaster toStatus =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus.UNDER_REVIEW.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status UNDER_REVIEW not found"));

        complaint.setStatus(toStatus);

        repository.save(complaint);

        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                toStatus.getStatusCode(),
                null,
                null,
                toStatus.getStatusCode());

        return complaint.toDTO();
    }
    @Transactional
    public ComplaintRegistrationDto scheduleInvestigation(
            Long id,
            LocalDate investigationDate,
            String remarks) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ApplicationStatusMaster status =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus.INVESTIGATION_SCHEDULED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status INVESTIGATION_SCHEDULED not found"));

        complaint.setStatus(status);

        repository.save(complaint);

        return complaint.toDTO();
    }
    @Transactional
    public ComplaintRegistrationDto approveComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ApplicationStatusMaster currentStatus =
                complaint.getStatus();

        if (currentStatus == null ||
                !GrievanceApplicationStatus.VERIFIED_BY_CVO.name()
                        .equalsIgnoreCase(currentStatus.getStatusCode())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Complaint must be VERIFIED_BY_CVO before final approval");
        }

        ApplicationStatusMaster approvedStatus =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus.APPLICATION_APPROVED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_APPROVED not found"));

        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        complaint.setStatus(approvedStatus);

        repository.save(complaint);

        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                approvedStatus.getStatusCode(),
                null,
                null,
                approvedStatus.getStatusCode());

        return complaint.toDTO();
    }


    @Transactional
    public ComplaintRegistrationDto rejectComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        ApplicationStatusMaster currentStatus =
                complaint.getStatus();

        if (currentStatus == null ||
                !GrievanceApplicationStatus.VERIFIED_BY_CVO.name()
                        .equalsIgnoreCase(currentStatus.getStatusCode())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Complaint must be VERIFIED_BY_CVO before final rejection");
        }

        ApplicationStatusMaster rejectedStatus =
                statusRepository.findByStatusCode(
                        GrievanceApplicationStatus.APPLICATION_REJECTED.name())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Status APPLICATION_REJECTED not found"));

        ApplicationStatusMaster fromStatus =
                complaint.getStatus();

        complaint.setStatus(rejectedStatus);

        repository.save(complaint);

        historyService.logStatusChange(
                complaint.getId(),
                fromStatus != null
                        ? fromStatus.getStatusCode()
                        : null,
                rejectedStatus.getStatusCode(),
                null,
                null,
                rejectedStatus.getStatusCode());

        return complaint.toDTO();
    }
}