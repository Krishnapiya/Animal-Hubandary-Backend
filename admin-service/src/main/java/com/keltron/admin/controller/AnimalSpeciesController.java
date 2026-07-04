package com.keltron.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.admin.services.impl.AnimalSpeciesServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.AnimalSpeciesDto;
import com.keltron.utility.beans.searchbean.AnimalSpeciesSearchBean;
import com.keltron.utility.jpa.predicates.AnimalSpeciesPredicates;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;

@RestController
@RequestMapping("admin/auth/master/animal-species")
public class AnimalSpeciesController {

    @Autowired
    private AnimalSpeciesServiceImpl service;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<AnimalSpeciesDto> request) {

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
                        service.save(
                                request.getPayLoad())
                                .toDTO())
                .build();
    }

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<AnimalSpeciesDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.PATCH))) {

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST)
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        service.update(
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

            @Valid AnimalSpeciesSearchBean searchBean) {

        int pageNo =
                searchBean.getPageNo() != null
                        ? searchBean.getPageNo()
                        : 0;

        int pageSize =
                searchBean.getPageSize() != null
                        ? searchBean.getPageSize()
                        : 25;

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                service.findByCriteria(
                                        AnimalSpeciesPredicates
                                                .createPredicate(searchBean),
                                                searchBean.getDataSort() != null
                                                ? searchBean.getDataSort()
                                                : Sort.by("id"),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                service.findByCriteria(
                                        AnimalSpeciesPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        pageNo,
                                        pageSize))
                        .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(service.delete(id))
                .build();
    }

    @GetMapping("/test")
    public String test() {
        return "Animal Species Controller Working";
    }
}