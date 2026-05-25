package com.keltron.admin.controller;

import java.io.ByteArrayOutputStream;
import java.util.Set;

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
import com.keltron.admin.services.impl.DesignationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.DesignationDto;
import com.keltron.utility.beans.searchbean.DesignationSearchBean;
import com.keltron.utility.jpa.predicates.DesignationPredicates;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "admin/auth/master/designation")
public class DesignationController extends AbstractController {

    @Autowired
    private DesignationServiceImpl serviceImpl;

    @PostMapping("/save")
    @RequirePermission(menu = "designation", action = "save")
    public ResponseEntity<AbstractResponse> save(@Valid @RequestBody Request<DesignationDto> request) {
        DesignationDto dto = request.getPayLoad();
        if (dto == null || !(request.isValid() && dto.isValid(HttpMethod.POST))) {
            Set<String> errors = dto != null && dto.getErrors() != null ? dto.getErrors() : Set.of("Invalid request");
            return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST, errors).build();
        }
        return new ResponseBuilder().withData(serviceImpl.save(dto).toDTO()).build();
    }

    @PatchMapping("/save")
    @RequirePermission(menu = "designation", action = "edit")
    public ResponseEntity<AbstractResponse> update(@Valid @RequestBody Request<DesignationDto> request) {
        DesignationDto dto = request.getPayLoad();
        if (dto == null || !(request.isValid() && dto.isValid(HttpMethod.PATCH))) {
            return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseBuilder().withData(serviceImpl.update(dto.getId(), dto).toDTO()).build();
    }

    @GetMapping("/list/all")
    @RequirePermission(menu = "designation", action = "list")
    public ResponseEntity<AbstractResponse> findByCriteria(
        @RequestParam(name = "dropDown", required = false, defaultValue = "false") boolean asDropdown,
        @Valid DesignationSearchBean searchBean) {
        int pageNo = searchBean.getPageNo() != null ? searchBean.getPageNo() : 0;
        int pageSize = searchBean.getPageSize() != null ? searchBean.getPageSize() : 25;
        return asDropdown
            ? new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                    DesignationPredicates.createPredicate(searchBean),
                    searchBean.getDataSort(),
                    asDropdown,
                    pageNo,
                    pageSize))
                .build()
            : new ResponseBuilder()
                .withData(serviceImpl.findByCriteria(
                    DesignationPredicates.createPredicate(searchBean),
                    searchBean.getDataSort(),
                    pageNo,
                    pageSize))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(menu = "designation", action = "delete")
    public ResponseEntity<AbstractResponse> delete(@Valid @PathVariable Integer id) {
        return new ResponseBuilder().withData(serviceImpl.delete(id)).build();
    }

    @PostMapping("/download-excel")
    @RequirePermission(menu = "designation", action = "export")
    public ResponseEntity<ByteArrayResource> downloadExcel(@RequestBody ExcelExportRequest request) {
        ByteArrayOutputStream out = serviceImpl.generateExcel(request);
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=designation.xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(resource.contentLength())
            .body(resource);
    }
}
