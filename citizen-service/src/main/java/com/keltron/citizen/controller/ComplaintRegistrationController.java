package com.keltron.citizen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.keltron.citizen.services.impl.ComplaintInvestigationServiceImpl;
import com.keltron.citizen.dto.ComplaintInvestigationDto;
import com.keltron.citizen.dto.ComplaintRegistrationDto;
import com.keltron.citizen.services.impl.ComplaintRegistrationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/citizen/auth/complaint-registration")
@Validated
public class ComplaintRegistrationController {

    @Autowired
    private ComplaintRegistrationServiceImpl serviceImpl;
    @Autowired
    private ComplaintInvestigationServiceImpl investigationService;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<ComplaintRegistrationDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.save(
                                request.getPayLoad())
                                .toDTO())
                .build();
    }

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<ComplaintRegistrationDto> request) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.update(
                                request.getPayLoad().getId(),
                                request.getPayLoad())
                                .toDTO())
                .build();
    }

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> list() {

        return new ResponseBuilder()
                .withData(serviceImpl.getComplaints())
                .build();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<AbstractResponse> view(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.getComplaint(id))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.deleteComplaint(id))
                .build();
    }
    @PatchMapping("/forward/{id}")
    public ResponseEntity<AbstractResponse> forwardComplaint(
            @PathVariable Long id) {

        System.out.println("===== COMPLAINT FORWARD CONTROLLER HIT =====");
        System.out.println("Complaint ID = " + id);

        return new ResponseBuilder()
                .withData(serviceImpl.forwardComplaint(id))
                .build();
    }
    @PatchMapping("/start-review/{id}")
    public ResponseEntity<AbstractResponse> startReview(
            @PathVariable Long id) {

        System.out.println("====================================");
        System.out.println("START REVIEW CONTROLLER HIT");
        System.out.println("Complaint ID = " + id);
        System.out.println("====================================");

        return new ResponseBuilder()
                .withData(serviceImpl.startReview(id))
                .build();
    }
    @PatchMapping("/schedule-investigation/{id}")
    public ResponseEntity<AbstractResponse> scheduleInvestigation(
            @PathVariable Long id,
            @RequestBody ComplaintInvestigationDto request) {

        System.out.println("====================================");
        System.out.println("SCHEDULE INVESTIGATION CONTROLLER HIT");
        System.out.println("Complaint ID = " + id);
        System.out.println(
                "Investigation Date = "
                        + request.getInvestigationDate());
        System.out.println(
                "Investigation Remarks = "
                        + request.getInvestigationRemarks());
        System.out.println("====================================");

        return new ResponseBuilder()
                .withData(
                        investigationService.scheduleInvestigation(
                                id,
                                request.getInvestigationDate(),
                                request.getInvestigationRemarks()))
                .build();
    }
    // ============================================
    // ADMIN FINAL APPROVAL
    // VERIFIED_BY_CVO → APPLICATION_APPROVED
    // ============================================
    @PatchMapping("/approve/{id}")
    public ResponseEntity<AbstractResponse> approveComplaint(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.approveComplaint(id))
                .build();
    }


    // ============================================
    // ADMIN FINAL REJECTION
    // VERIFIED_BY_CVO → APPLICATION_REJECTED
    // ============================================
    @PatchMapping("/reject/{id}")
    public ResponseEntity<AbstractResponse> rejectComplaint(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.rejectComplaint(id))
                .build();
    }
}