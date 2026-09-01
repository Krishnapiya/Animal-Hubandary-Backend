package com.keltron.petshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.keltron.petshop.dto.RegistrationApplicationResubmissionDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// ************* CHANGED *************
import com.keltron.petshop.dto.PetShopRegistrationApplicationDto;
import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.keltron.petshop.predicates.PetShopRegistrationApplicationPredicates;
import com.keltron.petshop.searchbean.PetShopRegistrationApplicationSearchBean;
import com.keltron.petshop.services.impl.PetShopRegistrationApplicationServiceImpl;
import com.keltron.petshop.services.impl.PetShopRegistrationPdfService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;
@RestController

// ************* CHANGED *************
@RequestMapping("petshop/auth/registration-application")

//@PreAuthorize("hasRole('ADMIN')")

// ************* CHANGED *************
public class PetShopRegistrationApplicationController
        extends AbstractController {

    
    @Autowired
    private PetShopRegistrationPdfService pdfService;

    // ************* CHANGED *************
    @Autowired
    private PetShopRegistrationApplicationServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<PetShopRegistrationApplicationDto> request) {

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
            @Valid @RequestBody Request<PetShopRegistrationApplicationDto> request) {

        System.out.println("PATCH DTO = " + request.getPayLoad());

        return new ResponseBuilder()
                .withData(
                        serviceImpl.update(
                                request.getPayLoad().getId(),
                                request.getPayLoad())
                                .toDTO())
                .build();
    }
//  @PatchMapping("/save")
//  public ResponseEntity<AbstractResponse> update(
//          @Valid @RequestBody Request<RegistrationApplicationDto> request) {
//
//      if (!(request.isValid()
//              && request.getPayLoad().isValid(HttpMethod.PATCH))) {
//
//          return new ResponseBuilder()
//                  .withError(HttpStatus.BAD_REQUEST)
//                  .build();
//      }
//
//      return new ResponseBuilder()
//              .withData(
//                      serviceImpl.update(
//                              request.getPayLoad().getId(),
//                              request.getPayLoad())
//                              .toDTO())
//              .build();
//  }

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @RequestParam(
                    name = "forwardedOnly",
                    required = false,
                    defaultValue = "false")
            boolean forwardedOnly,

            @RequestParam(
                    name = "status",
                    required = false)
            String status,

            @Valid PetShopRegistrationApplicationSearchBean searchBean) {
    	System.out.println("forwardedOnly = " + forwardedOnly);

    	if (forwardedOnly) {
    	    return new ResponseBuilder()
    	            .withData(serviceImpl.getMyForwardedApplications(status))
    	            .build();
    	}
        return new ResponseBuilder()
                .withData(
                        serviceImpl.getPetShopApplications(status))
                .build();
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<AbstractResponse> viewApplication(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.getApplication(id))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @Valid @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadApplication(
            @PathVariable Long id)
            throws Exception {

        PetShopRegistrationViewDto view =
                serviceImpl.getApplication(id);
        byte[] zipBytes = pdfService.generateApplicationZip(id);

        String fileName = view.getApplicationNumber() != null
                && !view.getApplicationNumber().isBlank()
                ? view.getApplicationNumber() + ".zip"
                : "PetShopApplication-" + id + ".zip";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zipBytes);
    }
    @PatchMapping("/forward/{id}")
    public ResponseEntity<AbstractResponse> forwardApplication(
            @PathVariable Long id) {

        System.out.println("===== FORWARD CONTROLLER HIT =====");
        System.out.println("Application ID = " + id);

        return new ResponseBuilder()
                .withData(serviceImpl.forwardApplication(id))
                .build();
    }
    @GetMapping("/my-forwarded")
    public ResponseEntity<AbstractResponse> getMyForwardedApplications(

            @RequestParam(
                    name = "status",
                    required = false)
            String status) {

        return new ResponseBuilder()
                .withData(serviceImpl.getMyForwardedApplications(status))
                .build();
    }
    @PatchMapping("/submit/{id}")
    public ResponseEntity<AbstractResponse> submitApplication(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.submitApplication(id))
                .build();
    }
    
    @PatchMapping("/resubmit")
    public ResponseEntity<AbstractResponse> resubmitApplication(
            @Valid
            @RequestBody
            Request<RegistrationApplicationResubmissionDto> request) {

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
                        serviceImpl.resubmitApplication(
                                request.getPayLoad()))
                .build();
    }
    
    @GetMapping("/my-applications")
    public ResponseEntity<AbstractResponse> myApplications(

            @RequestParam(
                    name = "status",
                    required = false)
            String status) {

        return new ResponseBuilder()
                .withData(serviceImpl.getMyApplications(status))
                .build();
    }
    @PostMapping("/approve/{id}")
    public ResponseEntity<AbstractResponse> approve(@PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.approveApplication(id))
                .build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<AbstractResponse> reject(@PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.rejectApplication(id))
                .build();
    }
    
    
    
}