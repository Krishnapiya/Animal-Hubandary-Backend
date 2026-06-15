package com.keltron.dogbreeder.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.dogbreeder.dto.DogBreederBreedDto;
import com.keltron.dogbreeder.predicates.DogBreederBreedPredicates;
import com.keltron.dogbreeder.searchbean.DogBreederBreedSearchBean;
import com.keltron.dogbreeder.services.impl.DogBreederBreedServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "dogbreeder/auth/master/dog-breeder-breed")
public class DogBreederBreedController extends AbstractController {

    @Autowired
    private DogBreederBreedServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(@Valid @RequestBody Request<DogBreederBreedDto> request) {

        if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.POST))) {
            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST, request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.save(request.getPayLoad()).toDTO())
                .build();
    }

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(@Valid @RequestBody Request<DogBreederBreedDto> request) {

        if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.PATCH))) {
            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST, request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl
                                .update(request.getPayLoad().getId(), request.getPayLoad())
                                .toDTO()
                )
                .build();
    }

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(name = "dropDown", required = false, defaultValue = "false")
            boolean asDropdown,

            @Valid DogBreederBreedSearchBean searchBean) {

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederBreedPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()
                                )
                        )
                        .build()
                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederBreedPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()
                                )
                        )
                        .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(@Valid @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }

    @PostMapping("/download-excel")
    public ResponseEntity<ByteArrayResource> downloadExcel(@RequestBody ExcelExportRequest request) {

        ByteArrayOutputStream out = serviceImpl.generateExcel(request);

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dog_breeder_breed.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .contentLength(resource.contentLength())
                .body(resource);
    }
}