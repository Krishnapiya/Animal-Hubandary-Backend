package com.keltron.dogbreeder.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationDto;
import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBrederRegistrationApplicationRepository;
import com.keltron.dogbreeder.repository.DogBreederApplicationDocumentRepository;
import com.keltron.dogbreeder.repository.DogBreederBreedRepository;
import com.keltron.dogbreeder.repository.DogBreederDeclarationRepository;
import com.keltron.dogbreeder.repository.DogBreederDetailRepository;
import com.keltron.dogbreeder.repository.DogBreederFacilityRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;


@Service
public class DogBreederRegistrationApplicationServiceImpl
        extends AbstractJpaService<
                DogBreederRegistrationApplicationDto,
                Long,
                DogBrederRegistrationApplicationRepository,
                DogBreederRegistrationApplication> {

    @Autowired
    private DogBrederRegistrationApplicationRepository applicationRepository;
    @Autowired
    private DogBreederDetailRepository detailRepository;

    @Autowired
    private DogBreederFacilityRepository facilityRepository;

    @Autowired
    private DogBreederDeclarationRepository declarationRepository;

    @Autowired
    private DogBreederBreedRepository breedRepository;

    @Autowired
    private DogBreederApplicationDocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<DogBreederRegistrationApplicationDto> getDogBreederApplications() {
        return applicationRepository
                .findByEntityTypeOrderByIdDesc("DOG_BREEDER")
                .stream()
                .map(DogBreederRegistrationApplication::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getLatestDogBreederApplicationId() {
        return applicationRepository
                .findTopByEntityTypeOrderByIdDesc("DOG_BREEDER")
                .map(DogBreederRegistrationApplication::getId)
                .orElseThrow(() ->
                        new RuntimeException("No dog breeder application found"));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPreview(Long applicationId) {

        DogBreederRegistrationApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Dog breeder application not found"));

        if (!"DOG_BREEDER".equals(application.getEntityType())) {
            throw new RuntimeException("Invalid dog breeder application");
        }

        Map<String, Object> response = new HashMap<>();

        response.put("registrationDetails", application.toDTO());

        var breederDetail = detailRepository
                .findByApplicationId(applicationId)
                .orElse(null);

        response.put(
                "breederDetails",
                breederDetail != null ? breederDetail.toDTO() : null
        );

        if (breederDetail != null) {
            Long dogBreederDetailId = breederDetail.getId();

            response.put(
                    "facilityDetails",
                    facilityRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .map(facility -> facility.toDTO())
                            .orElse(null)
            );

            response.put(
                    "declarationDetails",
                    declarationRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .map(declaration -> declaration.toDTO())
                            .orElse(null)
            );

            response.put(
                    "breedDetails",
                    breedRepository
                            .findByDogBreederDetail_Id(dogBreederDetailId)
                            .stream()
                            .map(breed -> breed.toDTO())
                            .toList()
            );
        }

        response.put(
                "documentDetails",
                documentRepository
                        .findByApplication_IdOrderByIdAsc(applicationId)
                        .stream()
                        .map(document -> document.toDTO())
                        .toList()
        );

        return response;
    }
    
}