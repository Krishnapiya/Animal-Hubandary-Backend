package com.keltron.dogbreeder.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.dogbreeder.dto.DogBreederDeclarationDto;
import com.keltron.dogbreeder.predicates.DogBreederDeclarationPredicates;
import com.keltron.dogbreeder.searchbean.DogBreederDeclarationSearchBean;
import com.keltron.dogbreeder.services.impl.DogBreederDeclarationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("dogbreeder/auth/master/dog-breeder-declaration")
public class DogBreederDeclarationController extends AbstractController {

    @Autowired
    private DogBreederDeclarationServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<DogBreederDeclarationDto> request) {

        DogBreederDeclarationDto dto = request.getPayLoad();

        if (dto == null || !(request.isValid()
                && dto.isValid(HttpMethod.POST))) {

            Set<String> errors =
                    dto != null && dto.getErrors() != null
                            ? dto.getErrors()
                            : Set.of("Invalid request");

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST, errors)
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.save(dto).toDTO())
                .build();
    }

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<DogBreederDeclarationDto> request) {

        DogBreederDeclarationDto dto = request.getPayLoad();

        if (dto == null || !(request.isValid()
                && dto.isValid(HttpMethod.PATCH))) {

            Set<String> errors =
                    dto != null && dto.getErrors() != null
                            ? dto.getErrors()
                            : Set.of("Invalid request");

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST, errors)
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.update(dto.getId(), dto).toDTO()
                )
                .build();
    }

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @Valid DogBreederDeclarationSearchBean searchBean) {

        int pageNo = searchBean.getPageNo() != null
                ? searchBean.getPageNo()
                : 0;

        int pageSize = searchBean.getPageSize() != null
                ? searchBean.getPageSize()
                : 25;

        if (searchBean.getDataSort() == null) {
            searchBean.setDataSort(
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederDeclarationPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()
                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederDeclarationPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        pageNo,
                                        pageSize))
                        .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @Valid @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }
}