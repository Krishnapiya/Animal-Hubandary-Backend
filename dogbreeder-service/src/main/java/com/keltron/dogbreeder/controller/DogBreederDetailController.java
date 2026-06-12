package com.keltron.dogbreeder.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.dogbreeder.dto.DogBreederDetailDto;
import com.keltron.dogbreeder.predicates.DogBreederDetailPredicates;
import com.keltron.dogbreeder.searchbean.DogBreederDetailSearchBean;
import com.keltron.dogbreeder.services.impl.DogBreederDetailServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "dogbreeder/auth/master/dog-breeder-detail")
public class DogBreederDetailController extends AbstractController {

    @Autowired
    private DogBreederDetailServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<DogBreederDetailDto> request) {

        DogBreederDetailDto dto = request.getPayLoad();

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
            @Valid @RequestBody Request<DogBreederDetailDto> request) {

        DogBreederDetailDto dto = request.getPayLoad();

        if (dto == null || !(request.isValid()
                && dto.isValid(HttpMethod.PATCH))) {

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST)
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.update(
                                dto.getId(),
                                dto)
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

            @Valid DogBreederDetailSearchBean searchBean) {

        int pageNo =
                searchBean.getPageNo() != null
                        ? searchBean.getPageNo()
                        : 0;

        int pageSize =
                searchBean.getPageSize() != null
                        ? searchBean.getPageSize()
                        : 25;

        if (searchBean.getDataSort() == null) {
            searchBean.setDataSort(
                    Sort.by(Sort.Direction.DESC, "id"));
        }

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederDetailPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DogBreederDetailPredicates.createPredicate(searchBean),
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