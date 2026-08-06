package com.keltron.dogbreeder.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationDto;
import com.keltron.dogbreeder.predicates.DogBreederRegistrationApplicationPredicates;
import com.keltron.dogbreeder.searchbean.DogBreederRegistrationApplicationSearchBean;
import com.keltron.dogbreeder.services.impl.DogBreederRegistrationApplicationPdfService;
import com.keltron.dogbreeder.services.impl.DogBreederRegistrationApplicationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;
import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationResubmissionDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("dogbreeder/auth/registration-application")
public class DogBreederRegistrationApplicationController
        extends AbstractController {

    private static final String ADMIN_AUTHORITY =
            "hasAnyAuthority('ADMIN', 'ROLE_ADMIN')";

    private static final String ADMIN_OR_CVO_AUTHORITY =
            "hasAnyAuthority("
                    + "'ADMIN', 'ROLE_ADMIN', "
                    + "'CVO', 'ROLE_CVO'"
                    + ")";

    @Autowired
    private DogBreederRegistrationApplicationServiceImpl serviceImpl;

    @Autowired
    private DogBreederRegistrationApplicationPdfService pdfService;
    
    @Autowired
    private UsersRepository usersRepository;

    /**
     * Save application — Admin only.
     */
    @PostMapping("/save")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> save(
            @Valid
            @RequestBody
            Request<DogBreederRegistrationApplicationDto> request) {

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
                        serviceImpl
                                .save(request.getPayLoad())
                                .toDTO())
                .build();
    }

    /**
     * Update application — Admin only.
     */
    @PatchMapping("/save")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> update(
            @Valid
            @RequestBody
            Request<DogBreederRegistrationApplicationDto> request) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl
                                .update(
                                        request.getPayLoad().getId(),
                                        request.getPayLoad())
                                .toDTO())
                .build();
    }

    /**
     * Admin list.
     */
    /**
     * Admin list.
     */
    @GetMapping("/list/all")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> findByCriteria(
            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,
            @Valid DogBreederRegistrationApplicationSearchBean searchBean) {

        searchBean.setEntityType("DOG_BREEDER");

        if (asDropdown) {
            return new ResponseBuilder()
                    .withData(
                            serviceImpl.findByCriteria(
                                    DogBreederRegistrationApplicationPredicates
                                            .createPredicate(searchBean),
                                    searchBean.getDataSort(),
                                    true,
                                    searchBean.getPageNo(),
                                    searchBean.getPageSize()))
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.findByCriteria(
                                DogBreederRegistrationApplicationPredicates
                                        .createPredicate(searchBean),
                                searchBean.getDataSort(),
                                searchBean.getPageNo(),
                                searchBean.getPageSize()))
                .build();
    }
    @GetMapping("/download/{applicationId}")
    public ResponseEntity<byte[]> download(@PathVariable Long applicationId) {

        byte[] zipBytes = pdfService.generateApplicationZip(applicationId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"dog-breeder-application-"
                                + applicationId
                                + ".zip\"")
                .body(zipBytes);
    }
    /**
     * Admin and CVO application preview.
     */
    @GetMapping("/preview/{applicationId}")
    @PreAuthorize(ADMIN_OR_CVO_AUTHORITY)
    public ResponseEntity<AbstractResponse> preview(
            @PathVariable Long applicationId) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getPreview(applicationId))
                .build();
    }

    /**
     * Admin and CVO PDF download.
     */
//    @GetMapping("/download/{applicationId}")
//    @PreAuthorize(ADMIN_OR_CVO_AUTHORITY)
//    public ResponseEntity<byte[]> download(
//            @PathVariable Long applicationId) {
//
//        byte[] pdfBytes =
//                pdfService.generateApplicationPdf(applicationId);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=dog-breeder-application-"
//                                + applicationId
//                                + ".pdf")
//                .body(pdfBytes);
//    }

    /**
     * Forward application — Admin only.
     */
    @PostMapping("/{applicationId}/forward")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> forwardToCvo(
            @PathVariable Long applicationId) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.forwardToCvo(applicationId))
                .build();
    }

    /**
     * CVO list.
     *
     * District ID is taken from the logged-in CVO JWT.
     * The frontend must not send districtId.
     */
    @Transactional
    @GetMapping("/cvo/list/all")
    public ResponseEntity<AbstractResponse> findCvoForwardedApplications(
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getSubject();

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Office office = user.getOffice();

        if (office == null) {
            throw new RuntimeException("Office not mapped to user");
        }

        District district = office.getDistrict();  

        if (district == null) {
            throw new RuntimeException("District not mapped to Office");
        }

        Integer districtId = district.getId();

        return new ResponseBuilder()
                .withData(serviceImpl.getCvoForwardedApplications(districtId))
                .build();
    }
    
    /**
     * Submit application.
     * Changes status from DRAFT to SUBMITTED.
     */
    @PostMapping("/submit/{applicationId}")
    public ResponseEntity<AbstractResponse> submitApplication(
            @PathVariable("applicationId") Long applicationId) {

        return new ResponseBuilder()
                .withData(serviceImpl.submitApplication(applicationId))
                .build();
    }
    @GetMapping("/my-applications")
    public ResponseEntity<AbstractResponse> myApplications(
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getSubject();

        return new ResponseBuilder()
                .withData(serviceImpl.getMyApplications(username))
                .build();
    }
    
    @PostMapping("/approve/{id}")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> approveApplication(
            @PathVariable("id") Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.approveApplication(id))
                .build();
    }

    /**
     * Final Rejection by Admin
     */
    @PostMapping("/reject/{id}")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> rejectApplication(
            @PathVariable("id") Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.rejectApplication(id))
                .build();
    }
    @PatchMapping("/resubmit")
    public ResponseEntity<AbstractResponse> resubmitApplication(
            @Valid
            @RequestBody
            Request<DogBreederRegistrationApplicationResubmissionDto> request) {

        if (request == null || request.getPayLoad() == null) {
            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            "Request payload is missing")
                    .build();
        }

        if (!request.isValid()
                || !request.getPayLoad().isValid(HttpMethod.PATCH)) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.resubmitApplication(request.getPayLoad()))
                .build();
    }
    /**
     * Delete application — Admin only.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }
}