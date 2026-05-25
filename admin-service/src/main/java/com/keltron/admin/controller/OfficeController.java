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
import com.keltron.admin.services.impl.OfficeServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.OfficeDto;
import com.keltron.utility.beans.searchbean.OfficeSearchBean;
import com.keltron.utility.jpa.predicates.OfficePredicates;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "admin/auth/master/office")
public class OfficeController extends AbstractController {

    @Autowired
    private OfficeServiceImpl serviceImpl;

    @PostMapping("/save")
    @RequirePermission(menu = "office", action = "save")
    public ResponseEntity<AbstractResponse> save(@Valid @RequestBody Request<OfficeDto> request) {
        if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.POST))) {
            return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST, request.getPayLoad().getErrors()).build();
        }
        return new ResponseBuilder().withData(serviceImpl.save(request.getPayLoad()).toDTO()).build();
    }

    @PatchMapping("/save")
    @RequirePermission(menu = "office", action = "edit")
    public ResponseEntity<AbstractResponse> update(@Valid @RequestBody Request<OfficeDto> request) {
        if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.PATCH))) {
            return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseBuilder()
            .withData(serviceImpl.update(request.getPayLoad().getId(), request.getPayLoad()).toDTO()).build();
    }

    @GetMapping("/list/all")
    @RequirePermission(menu = "office", action = "list")
    public ResponseEntity<AbstractResponse> findByCriteria(
        @RequestParam(name = "dropDown", required = false, defaultValue = "false") boolean asDropdown,
        @Valid OfficeSearchBean searchBean) {
        return asDropdown
            ? new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                    OfficePredicates.createPredicate(searchBean),
                    searchBean.getDataSort(), asDropdown, searchBean.getPageNo(), searchBean.getPageSize()))
                .build()
            : new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                    OfficePredicates.createPredicate(searchBean),
                    searchBean.getDataSort(), searchBean.getPageNo(), searchBean.getPageSize()))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(menu = "office", action = "delete")
    public ResponseEntity<AbstractResponse> delete(@Valid @PathVariable Integer id) {
        return new ResponseBuilder().withData(serviceImpl.delete(id)).build();
    }

    @PostMapping("/download-excel")
    @RequirePermission(menu = "office", action = "export")
    public ResponseEntity<ByteArrayResource> downloadExcel(@RequestBody ExcelExportRequest request) {
        ByteArrayOutputStream out = serviceImpl.generateExcel(request);
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=office.xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(resource.contentLength())
            .body(resource);
    }
}
