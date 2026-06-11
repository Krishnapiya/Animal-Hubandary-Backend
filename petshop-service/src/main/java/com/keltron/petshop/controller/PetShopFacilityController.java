package com.keltron.petshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.petshop.dto.PetShopFacilityDto;
import com.keltron.petshop.searchbean.PetShopFacilitySearchBean;
import com.keltron.petshop.services.impl.PetShopFacilityServiceImpl;
import com.keltron.petshop.predicates.PetShopFacilityPredicates;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("petshop/auth/master/pet-shop-facility")
public class PetShopFacilityController
        extends AbstractController {

    @Autowired
    private PetShopFacilityServiceImpl serviceImpl;

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<PetShopFacilityDto> request) {

        if (!(request.isValid()
                && request.getPayLoad()
                        .isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad()
                                    .getErrors())
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
            @Valid @RequestBody Request<PetShopFacilityDto> request) {

        if (!(request.isValid()
                && request.getPayLoad()
                        .isValid(HttpMethod.PATCH))) {

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST)
                    .build();
        }

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
            @Valid PetShopFacilitySearchBean searchBean) {

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
                    Sort.by(
                            Sort.Direction.DESC,
                            "id"));
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl.findByCriteria(
                                PetShopFacilityPredicates
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
                .withData(
                        serviceImpl.delete(id))
                .build();
    }
}