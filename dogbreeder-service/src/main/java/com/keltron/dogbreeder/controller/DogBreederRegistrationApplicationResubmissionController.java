package com.keltron.dogbreeder.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationResubmissionUploadDto;
import com.keltron.dogbreeder.services.impl.DogBreederRegistrationApplicationResubmissionServiceImpl;

@RestController
@RequestMapping("/dogbreeder/auth/awb/resubmission")
@CrossOrigin(origins = "*")
public class DogBreederRegistrationApplicationResubmissionController {

    @Autowired
    private DogBreederRegistrationApplicationResubmissionServiceImpl service;

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DogBreederRegistrationApplicationResubmissionUploadDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicationId") Long applicationId,
            @RequestParam(value = "remarks", required = false) String remarks) throws IOException {

        return ResponseEntity.ok(
                service.uploadDocument(file, applicationId, remarks)
        );
    }
}