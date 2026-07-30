package com.keltron.petshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.petshop.dto.RegistrationInspectionDto;
import com.keltron.petshop.services.impl.RegistrationInspectionServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("petshop/auth/registration-inspection")
public class RegistrationInspectionController extends AbstractController {

    @Autowired
    private RegistrationInspectionServiceImpl serviceImpl;

    /**
     * Save Inspection Schedule
     */
    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<RegistrationInspectionDto> request) {

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

    /**
     * Update Inspection
     */
    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<RegistrationInspectionDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.PATCH))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.update(
                                request.getPayLoad().getId(),
                                request.getPayLoad())
                                .toDTO())
                .build();
    }

    /**
     * Get Inspection By Id
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<AbstractResponse> view(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.get(id).toDTO())
                .build();
    }

    /**
     * Get Inspection By Application Id
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<AbstractResponse> getByApplication(
            @PathVariable Long applicationId) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getByApplication(applicationId))
                .build();
    }

    /**
     * List All Inspections
     */
    @GetMapping("/list")
    public ResponseEntity<AbstractResponse> list() {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getAll())
                .build();
    }

    /**
     * Delete Inspection
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.delete(id))
                .build();
    }
    @PostMapping("/upload-report")
    public ResponseEntity<AbstractResponse> uploadInspectionReport(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("reportFile") MultipartFile reportFile,
            @RequestParam("remarks") String remarks,
            @RequestParam("recommendation") String recommendation) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.uploadInspectionReport(
                                applicationId,
                                reportFile,
                                remarks,
                                recommendation)
                                .toDTO())
                .build();
    }

}