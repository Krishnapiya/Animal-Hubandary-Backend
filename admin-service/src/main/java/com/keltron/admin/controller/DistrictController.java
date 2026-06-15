package com.keltron.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.admin.rbac.security.RequirePermission;
import com.keltron.admin.services.impl.DistrictServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.DistrictDto;
import com.keltron.utility.beans.searchbean.DistrictSearchBean;
import com.keltron.utility.jpa.predicates.DistrictPredicates;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("admin/auth/master/district")
public class DistrictController extends AbstractController {

    @Autowired
    private DistrictServiceImpl serviceImpl;

    @PostMapping("/save")
    @RequirePermission(menu = "district", action = "save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<DistrictDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.save(
                        request.getPayLoad()).toDTO())
                .build();
    }

    @PatchMapping("/save")
    @RequirePermission(menu = "district", action = "edit")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<DistrictDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.PATCH))) {

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
    @RequirePermission(menu = "district", action = "list")
    public ResponseEntity<AbstractResponse> findByCriteria(
            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @Valid DistrictSearchBean searchBean) {

        int pageNo = searchBean.getPageNo() != null ? searchBean.getPageNo() : 0;
        int pageSize = searchBean.getPageSize() != null ? searchBean.getPageSize() : 25;

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DistrictPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        DistrictPredicates.createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        pageNo,
                                        pageSize))
                        .build();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(menu = "district", action = "delete")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Integer id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }
}
