package com.keltron.citizen.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.citizen.dto.ComplaintInvestigationDto;
import com.keltron.citizen.services.impl.ComplaintInvestigationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.responses.AbstractResponse;

@RestController
@RequestMapping("/citizen/auth/complaint-investigation")
@Validated
public class ComplaintInvestigationController {

    @Autowired
    private ComplaintInvestigationServiceImpl serviceImpl;

    // =========================================================
    // SCHEDULE INVESTIGATION
    // =========================================================

    @PatchMapping("/schedule/{complaintId}")
    public ResponseEntity<AbstractResponse> scheduleInvestigation(
            @PathVariable Long complaintId,
            @RequestBody ComplaintInvestigationDto request) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.scheduleInvestigation(
                                complaintId,
                                request.getInvestigationDate(),
                                request.getInvestigationRemarks()))
                .build();
    }

    // =========================================================
    // UPLOAD INVESTIGATION REPORT
    // =========================================================

    @PostMapping(
            value = "/upload/{complaintId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AbstractResponse> uploadInvestigationReport(

            @PathVariable Long complaintId,

            @RequestParam("reportFile")
            MultipartFile reportFile,

            @RequestParam("remarks")
            String remarks,

            @RequestParam("recommendation")
            String recommendation)
            throws IOException {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.uploadInvestigationReport(
                                complaintId,
                                reportFile,
                                remarks,
                                recommendation))
                .build();
    }

    // =========================================================
    // VIEW
    // =========================================================

    @GetMapping("/view/{id}")
    public ResponseEntity<AbstractResponse> view(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getInvestigation(id))
                .build();
    }

    // =========================================================
    // VIEW BY COMPLAINT
    // =========================================================

    @GetMapping("/view-by-complaint/{complaintId}")
    public ResponseEntity<AbstractResponse> viewByComplaint(
            @PathVariable Long complaintId) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getByComplaint(complaintId))
                .build();
    }

    // =========================================================
    // LIST
    // =========================================================

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> list() {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getInvestigations())
                .build();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.deleteInvestigation(id))
                .build();
    }
}