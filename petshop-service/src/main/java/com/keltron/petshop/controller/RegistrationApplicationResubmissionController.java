package com.keltron.petshop.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.petshop.dto.RegistrationApplicationResubmissionUploadDto;
import com.keltron.petshop.services.impl.RegistrationApplicationResubmissionServiceImpl;

@RestController
@RequestMapping("/petshop/auth/registration-application/resubmission")
public class RegistrationApplicationResubmissionController {

    @Autowired
    private RegistrationApplicationResubmissionServiceImpl service;

    @PostMapping("/upload")
    public ResponseEntity<RegistrationApplicationResubmissionUploadDto>
            uploadDocument(

                    @RequestParam("file")
                    MultipartFile file,

                    @RequestParam("applicationId")
                    Long applicationId)
                    throws IOException {

        return ResponseEntity.ok(
                service.uploadDocument(
                        file,
                        applicationId));
    }
}