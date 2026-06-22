package com.keltron.dogbreeder.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.dogbreeder.dto.DogBreederRegistrationStep1Dto;
import com.keltron.dogbreeder.services.impl.DogBreederOwnerApplicationService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("dogbreeder/auth/awb/dog-breeder/registration")
@RequiredArgsConstructor
public class DogBreederOwnerRegistrationController {

    private final DogBreederOwnerApplicationService registrationService;

    @GetMapping("/draft")
    public ResponseEntity<?> getDraft() {
        return new ResponseBuilder()
                .withData(registrationService.getDraftStep1())
                .build();
    }

    @PostMapping("/step-1")
    public ResponseEntity<?> saveStep1(
            @RequestBody Request<DogBreederRegistrationStep1Dto> request) {

        DogBreederRegistrationStep1Dto dto =
                request != null ? request.getPayLoad() : null;

        Map<String, String> errors = registrationService.validateStep1(dto);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return new ResponseBuilder()
                .withData(registrationService.saveStep1(dto))
                .build();
    }
}