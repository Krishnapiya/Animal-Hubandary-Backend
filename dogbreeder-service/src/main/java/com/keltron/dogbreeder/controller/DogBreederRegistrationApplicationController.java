package com.keltron.dogbreeder.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.keltron.dogbreeder.dto.DogBreederRegistrationApplicationDto;
import com.keltron.dogbreeder.predicates.DogBreederRegistrationApplicationPredicates;
import com.keltron.dogbreeder.searchbean.DogBreederRegistrationApplicationSearchBean;
import com.keltron.dogbreeder.services.impl.DogBreederRegistrationApplicationPdfService;
import com.keltron.dogbreeder.services.impl.DogBreederRegistrationApplicationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(value = "dogbreeder/auth/registration-application")
@PreAuthorize("hasRole('ADMIN')")
public class DogBreederRegistrationApplicationController extends AbstractController {
	
	 @Autowired
	    private DogBreederRegistrationApplicationServiceImpl serviceImpl;
	 
	 @Autowired
	 private DogBreederRegistrationApplicationPdfService pdfService;

	    @PostMapping("/save")
	    public ResponseEntity<AbstractResponse> save(
	            @Valid @RequestBody Request<DogBreederRegistrationApplicationDto> request) {

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
	            @Valid @RequestBody Request<DogBreederRegistrationApplicationDto> request) {

	        System.out.println("PATCH DTO = " + request.getPayLoad());

	        return new ResponseBuilder()
	                .withData(
	                        serviceImpl.update(
	                                request.getPayLoad().getId(),
	                                request.getPayLoad())
	                                .toDTO())
	                .build();
	    }


	    @GetMapping("/list/all")
	    public ResponseEntity<AbstractResponse> findByCriteria(
	            @RequestParam(
	                    name = "dropDown",
	                    required = false,
	                    defaultValue = "false")
	            boolean asDropdown,

	            @Valid DogBreederRegistrationApplicationSearchBean searchBean) {

	        searchBean.setEntityType("DOG_BREEDER");

	        return asDropdown
	                ? new ResponseBuilder()
	                        .withData(
	                                serviceImpl.findByCriteria(
	                                        DogBreederRegistrationApplicationPredicates
	                                                .createPredicate(searchBean),
	                                        searchBean.getDataSort(),
	                                        asDropdown,
	                                        searchBean.getPageNo(),
	                                        searchBean.getPageSize()))
	                        .build()
	                : new ResponseBuilder()
	                        .withData(
	                                serviceImpl.findByCriteria(
	                                        DogBreederRegistrationApplicationPredicates
	                                                .createPredicate(searchBean),
	                                        searchBean.getDataSort(),
	                                        searchBean.getPageNo(),
	                                        searchBean.getPageSize()))
	                        .build();
	    }
	    @GetMapping("/preview/{applicationId}")
	    public ResponseEntity<AbstractResponse> preview(
	            @PathVariable Long applicationId) {

	        return new ResponseBuilder()
	                .withData(serviceImpl.getPreview(applicationId))
	                .build();
	    }
	    @GetMapping("/download/{applicationId}")
	    public ResponseEntity<byte[]> download(@PathVariable Long applicationId) {

	        byte[] pdfBytes = pdfService.generateApplicationPdf(applicationId);

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(
	                        HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=dog-breeder-application-" + applicationId + ".pdf")
	                .body(pdfBytes);
	    }
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<AbstractResponse> delete(
	            @Valid @PathVariable Long id) {

	        return new ResponseBuilder()
	                .withData(serviceImpl.delete(id))
	                .build();
	    }
	   
	    
}
