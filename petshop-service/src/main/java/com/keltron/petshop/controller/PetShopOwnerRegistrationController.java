package com.keltron.petshop.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.petshop.dto.PetShopRegistrationStep1Dto;
import com.keltron.petshop.services.impl.PetShopOwnerApplicationService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("petshop/auth/awb/pet-shop/registration")
@RequiredArgsConstructor
public class PetShopOwnerRegistrationController {

    private final PetShopOwnerApplicationService registrationService;

    @GetMapping("/draft")
    public ResponseEntity<?> getDraft() {
        return new ResponseBuilder()
                .withData(registrationService.getDraftStep1())
                .build();
    }

    @PostMapping("/step-1")
    public ResponseEntity<?> saveStep1(
            @RequestBody Request<PetShopRegistrationStep1Dto> request) {

        PetShopRegistrationStep1Dto dto =
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
