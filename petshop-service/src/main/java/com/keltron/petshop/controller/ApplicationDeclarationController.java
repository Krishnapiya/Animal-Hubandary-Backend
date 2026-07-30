package com.keltron.petshop.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import com.keltron.petshop.dto.ApplicationDeclarationDto;
import com.keltron.petshop.predicates.ApplicationDeclarationPredicates;
import com.keltron.petshop.searchbean.ApplicationDeclarationSearchBean;
import com.keltron.petshop.services.impl.ApplicationDeclarationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
public class ApplicationDeclarationController
        extends AbstractController {

    @Autowired
    private ApplicationDeclarationServiceImpl serviceImpl;

    @PostMapping("petshop/auth/master/application-declaration/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<ApplicationDeclarationDto> request) {

        ApplicationDeclarationDto dto = request.getPayLoad();

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

    @PatchMapping("petshop/auth/master/application-declaration/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<ApplicationDeclarationDto> request) {

        ApplicationDeclarationDto dto = request.getPayLoad();

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

    @GetMapping("petshop/auth/master/application-declaration/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @Valid ApplicationDeclarationSearchBean searchBean) {

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

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        ApplicationDeclarationPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        pageNo,
                                        pageSize))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        ApplicationDeclarationPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        pageNo,
                                        pageSize))
                        .build();
    }

    @DeleteMapping(
            "petshop/auth/master/application-declaration/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @Valid @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }
    
    @GetMapping(
    	    "petshop/auth/master/application-declaration/draft/{applicationId}")
    	public ResponseEntity<AbstractResponse> getDraft(
    	        @PathVariable Long applicationId) {

    	    return new ResponseBuilder()
    	            .withData(
    	                    serviceImpl.getDraft(
    	                            applicationId))
    	            .build();
    	}

//    @PostMapping(
//            "petshop/auth/master/application-declaration/download-excel")
//    public ResponseEntity<ByteArrayResource> downloadExcel(
//            @RequestBody ExcelExportRequest request) {
//
//        ByteArrayOutputStream out =
//                serviceImpl.generateExcel(request);
//
//        ByteArrayResource resource =
//                new ByteArrayResource(out.toByteArray());
//
//        return ResponseEntity.ok()
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=application_declaration.xlsx")
//                .contentType(
//                        MediaType.parseMediaType(
//                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                .contentLength(resource.contentLength())
//                .body(resource);
//    }
}