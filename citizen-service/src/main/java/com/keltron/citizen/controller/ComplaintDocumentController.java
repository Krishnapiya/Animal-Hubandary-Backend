package com.keltron.citizen.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.citizen.dto.ComplaintDocumentDto;
import com.keltron.citizen.predicates.ComplaintDocumentPredicates;
import com.keltron.citizen.searchbean.ComplaintDocumentSearchBean;
import com.keltron.citizen.services.impl.ComplaintDocumentServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/citizen/auth/complaint-document")
public class ComplaintDocumentController extends AbstractController {

    @Autowired
    private ComplaintDocumentServiceImpl serviceImpl;

    // =========================================================
    // SAVE
    // =========================================================

    @PostMapping("/save")
    public ResponseEntity<AbstractResponse> save(
            @Valid @RequestBody Request<ComplaintDocumentDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.POST))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl
                                .save(request.getPayLoad())
                                .toDTO())
                .build();
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PatchMapping("/save")
    public ResponseEntity<AbstractResponse> update(
            @Valid @RequestBody Request<ComplaintDocumentDto> request) {

        if (!(request.isValid()
                && request.getPayLoad().isValid(HttpMethod.PATCH))) {

            return new ResponseBuilder()
                    .withError(
                            HttpStatus.BAD_REQUEST,
                            request.getPayLoad().getErrors())
                    .build();
        }

        return new ResponseBuilder()
                .withData(
                        serviceImpl
                                .update(
                                        request.getPayLoad().getId(),
                                        request.getPayLoad())
                                .toDTO())
                .build();
    }

    // =========================================================
    // LIST
    // =========================================================

    @GetMapping("/list/all")
    public ResponseEntity<AbstractResponse> findByCriteria(

            @RequestParam(
                    name = "dropDown",
                    required = false,
                    defaultValue = "false")
            boolean asDropdown,

            @Valid ComplaintDocumentSearchBean searchBean) {

        return asDropdown
                ? new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        ComplaintDocumentPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        asDropdown,
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()))
                        .build()

                : new ResponseBuilder()
                        .withData(
                                serviceImpl.findByCriteria(
                                        ComplaintDocumentPredicates
                                                .createPredicate(searchBean),
                                        searchBean.getDataSort(),
                                        searchBean.getPageNo(),
                                        searchBean.getPageSize()))
                        .build();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> delete(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.delete(id))
                .build();
    }

    // =========================================================
    // DRAFT
    // =========================================================

    @GetMapping("/draft/{complaintId}")
    public ResponseEntity<AbstractResponse> getDraft(
            @PathVariable Long complaintId) {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.getDraft(complaintId))
                .build();
    }

    // =========================================================
    // UPLOAD
    // =========================================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AbstractResponse> uploadDocument(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam Long complaintId,

            @RequestParam Long documentTypeId,

            @RequestParam Long uploadedBy)
            throws IOException {

        return new ResponseBuilder()
                .withData(
                        serviceImpl.uploadDocument(
                                file,
                                complaintId,
                                documentTypeId,
                                uploadedBy))
                .build();
    }

    // =========================================================
    // VIEW
    // =========================================================

    @GetMapping("/view/**")
    public ResponseEntity<Resource> viewDocument(
            HttpServletRequest request)
            throws IOException {

        String path = request.getRequestURI();

        String filePath =
                path.substring(
                        path.indexOf("/view/") + 6);

        filePath = URLDecoder.decode(
                filePath,
                StandardCharsets.UTF_8);

        System.out.println(
                "Decoded Path : " + filePath);

        return serviceImpl.viewDocument(filePath);
    }
}