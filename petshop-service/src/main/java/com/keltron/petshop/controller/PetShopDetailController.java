package com.keltron.petshop.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.petshop.services.impl.PetShopDetailServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.petshop.dto.PetShopDetailDto;
import com.keltron.petshop.searchbean.PetShopDetailSearchBean;
import com.keltron.petshop.predicates.PetShopDetailPredicates;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "petshop/auth/master/pet-shop-detail")
public class PetShopDetailController extends AbstractController {

    @Autowired
    private PetShopDetailServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<PetShopDetailDto> request) {

        PetShopDetailDto dto = request.getPayLoad();

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
            @Valid @RequestBody Request<PetShopDetailDto> request) {

        PetShopDetailDto dto = request.getPayLoad();

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

            @Valid PetShopDetailSearchBean searchBean) {

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
                                        PetShopDetailPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        PetShopDetailPredicates.createPredicate(searchBean),
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