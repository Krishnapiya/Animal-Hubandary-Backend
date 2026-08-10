package com.keltron.citizen.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.citizen.dto.ComplaintRegistrationDto;
import com.keltron.citizen.entity.ComplaintRegistration;
import com.keltron.citizen.repository.CitizenApplicationStatusMasterRepository;
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

    @Override
    @Transactional
    public ComplaintRegistration save(ComplaintRegistrationDto dto) {

        // Temporary until JWT Login is integrated
        dto.setCitizenUserId(1L);

        // Generate Complaint Number
        if (dto.getComplaintNumber() == null
                || dto.getComplaintNumber().isBlank()) {

            dto.setComplaintNumber("CMP" + System.currentTimeMillis());
        }

        // Default Status = SUBMITTED
        if (dto.getStatusId() == null) {

            ApplicationStatusMaster status =
                    statusRepository.findByStatusCode(
                                    GrievanceApplicationStatus.SUBMITTED.name())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "SUBMITTED status not found"));

            dto.setStatusId(status.getId());
        }

        return super.save(dto);
    }

    @Transactional(readOnly = true)
    public List<ComplaintRegistrationDto> getComplaints() {

        return repository.findAll()
                .stream()
                .map(ComplaintRegistration::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintRegistrationDto getComplaint(Long id) {

        ComplaintRegistration complaint =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Complaint not found"));

        return complaint.toDTO();
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
}