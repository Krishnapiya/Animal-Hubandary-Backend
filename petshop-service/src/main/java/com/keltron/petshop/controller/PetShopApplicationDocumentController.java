package com.keltron.petshop.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import com.keltron.admin.rbac.security.RequirePermission;
import com.keltron.petshop.dto.PetShopApplicationDocumentDto;
import com.keltron.petshop.predicates.PetShopApplicationDocumentPredicates;
import com.keltron.petshop.searchbean.PetShopApplicationDocumentSearchBean;
import com.keltron.petshop.services.impl.PetShopApplicationDocumentServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

@RestController

// ******** CHANGED ********
@RequestMapping("/petshop/auth/application-document")

// ******** CHANGED ********
public class PetShopApplicationDocumentController extends AbstractController {

    @Autowired
    private PetShopApplicationDocumentServiceImpl serviceImpl;

    // ===========================================================
    // SAVE
    // ===========================================================

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<PetShopApplicationDocumentDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(serviceImpl.save(request.getPayLoad()).toDTO())
                .build();
    }

    // ===========================================================
    // UPDATE
    // ===========================================================

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<PetShopApplicationDocumentDto> request) {

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

    // ===========================================================
    // LIST
    // ===========================================================

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @Valid PetShopApplicationDocumentSearchBean searchBean) {

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        PetShopApplicationDocumentPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        PetShopApplicationDocumentPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()))
                        .build();
    }

    // ===========================================================
    // DELETE
    // ===========================================================

    @DeleteMapping("/delete/{id}")
   // @RequirePermission(menu = "application-document", action = "delete")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.delete(id))
                .build();
    }

    // ===========================================================
    // EXCEL
    // ===========================================================

    @PostMapping("/download-excel")
   // @RequirePermission(menu = "application-document", action = "export")
    public ResponseEntity<ByteArrayResource> downloadExcel(
            @RequestBody ExcelExportRequest request) {

        ByteArrayOutputStream out =
                serviceImpl.generateExcel(request);

        ByteArrayResource resource =
                new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=application-document.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    // ===========================================================
    // DRAFT
    // ===========================================================

    @GetMapping("/draft/{applicationId}")
    public ResponseEntity<AbstractResponse> getDraft(
            @PathVariable Long applicationId) {

        return new ResponseBuilder()
                .withData(serviceImpl.getDraft(applicationId))
                .build();
    }

    // ===========================================================
    // UPLOAD
    // ===========================================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AbstractResponse> uploadDocument(

            @RequestParam("file") MultipartFile file,

            @RequestParam Long applicationId,

            @RequestParam Long documentTypeId,

            @RequestParam Long uploadedBy)
            throws IOException {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.uploadDocument(
                                file,
                                applicationId,
                                documentTypeId,
                                uploadedBy))
                .build();
    }

    // ===========================================================
    // VIEW
    // ===========================================================

    @GetMapping("/view/**")
    public ResponseEntity<Resource> viewDocument(
            HttpServletRequest request)
            throws IOException {
    	

        String path = request.getRequestURI();

        String filePath =
                path.substring(path.indexOf("/view/") + 6);

        // Decode URL-encoded characters (%20, %28, etc.)
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

        System.out.println("Decoded Path : " + filePath);

        return serviceImpl.viewDocument(filePath);
    }
}