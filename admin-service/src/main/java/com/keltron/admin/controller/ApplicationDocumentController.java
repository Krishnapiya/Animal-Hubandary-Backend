package com.keltron.admin.controller;

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

import com.keltron.admin.rbac.security.RequirePermission;
import com.keltron.admin.services.impl.ApplicationDocumentServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.ApplicationDocumentDto;
import com.keltron.utility.beans.searchbean.ApplicationDocumentSearchBean;
import com.keltron.utility.jpa.predicates.ApplicationDocumentPredicates;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "admin/auth/awb/application-document")
public class ApplicationDocumentController extends AbstractController {

    @Autowired
    private ApplicationDocumentServiceImpl serviceImpl;

    @PostMapping("/save")
    @RequirePermission(menu = "application-document", action = "save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<ApplicationDocumentDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.save(request.getPayLoad()).toDTO())
                .build();
    }

    @PatchMapping("/save")
    @RequirePermission(menu = "application-document", action = "edit")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<ApplicationDocumentDto> request) {

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
    @RequirePermission(menu = "application-document", action = "list")
    public ResponseEntity<AbstractResponse> findByCriteria(
        @RequestParam(name = "dropDown", required = false, defaultValue = "false") boolean asDropdown,
        @Valid ApplicationDocumentSearchBean searchBean) {
        return asDropdown
            ? new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                		ApplicationDocumentPredicates.createPredicate(searchBean),
                    searchBean.getDataSort(), asDropdown, searchBean.getPageNo(), searchBean.getPageSize()))
                .build()
            : new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                		ApplicationDocumentPredicates.createPredicate(searchBean),
                    searchBean.getDataSort(), searchBean.getPageNo(), searchBean.getPageSize()))
                .build();
    }
    @DeleteMapping("/delete/{id}")
    @RequirePermission(menu = "application-document", action = "delete")
    public ResponseEntity<AbstractResponse> delete(@Valid @PathVariable Long id) {
        return new ResponseBuilder().withData(serviceImpl.delete(id)).build();
    }


    @PostMapping("/download-excel")
    @RequirePermission(menu = "application-document", action = "export")
    public ResponseEntity<ByteArrayResource> downloadExcel(@RequestBody ExcelExportRequest request) {
        ByteArrayOutputStream out = serviceImpl.generateExcel(request);
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=application-document.xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-application-documentdocument.spreadsheetml.sheet"))
            .contentLength(resource.contentLength())
            .body(resource);
    }
}