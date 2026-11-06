package com.keltron.petshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopCertificateDraftDto;
import com.keltron.petshop.dto.PetShopCertificateDto;
import com.keltron.petshop.entity.PetShopCertificate;
import com.keltron.petshop.services.PetShopCertificateService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

@RestController
@RequestMapping("petshop/auth/registration-certificate")
public class PetShopCertificateController extends AbstractController {

    @Autowired
    private PetShopCertificateService certificateService;

    private boolean isOfficerOrAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority().toUpperCase();
            return r.contains("ADMIN") || r.contains("OFFICER") || r.contains("CVO");
        });
    }

    /**
     * Generate Certificate of Registration (Admin Only)
     */
    @PostMapping("/generate/{applicationId}")
    public ResponseEntity<AbstractResponse> generateCertificate(@PathVariable Long applicationId) {
        if (!isOfficerOrAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only Admin can generate certificates");
        }
        Long adminUserId = certificateService.getCurrentUserId();
        PetShopCertificateDto dto = certificateService.generateCertificate(applicationId, adminUserId);
        return new ResponseBuilder()
                .withData(dto)
                .build();
    }

    /**
     * Get Certificate Details / Status
     */
    @GetMapping("/details/{applicationId}")
    public ResponseEntity<AbstractResponse> getCertificateDetails(@PathVariable Long applicationId) {
        certificateService.validateCertificateAccess(applicationId);
        PetShopCertificateDto dto = certificateService.getCertificateDetails(applicationId);
        return new ResponseBuilder()
                .withData(dto)
                .build();
    }

    /**
     * Applicant Portal: Get My Certificate Details
     */
    @GetMapping("/my-certificate/{applicationId}")
    public ResponseEntity<AbstractResponse> getMyCertificate(@PathVariable Long applicationId) {
        certificateService.validateCertificateAccess(applicationId);
        PetShopCertificateDto dto = certificateService.getCertificateDetails(applicationId);
        return new ResponseBuilder()
                .withData(dto)
                .build();
    }

    /**
     * Preview Certificate PDF (inline)
     */
    @GetMapping("/preview/{applicationId}")
    public ResponseEntity<byte[]> previewCertificate(@PathVariable Long applicationId) {
        certificateService.validateCertificateAccess(applicationId);
        PetShopCertificate cert = certificateService.getCertificateEntity(applicationId);
        byte[] pdfBytes = certificateService.getCertificatePdfBytes(applicationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename(cert.getFileName())
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Download Certificate PDF (attachment)
     */
    @GetMapping("/download/{applicationId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long applicationId) {
        certificateService.validateCertificateAccess(applicationId);
        PetShopCertificate cert = certificateService.getCertificateEntity(applicationId);
        byte[] pdfBytes = certificateService.getCertificatePdfBytes(applicationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(cert.getFileName())
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Admin Preview Modal: Get Draft Certificate Details
     */
    @GetMapping("/draft-details/{applicationId}")
    public ResponseEntity<AbstractResponse> getDraftCertificateDetails(@PathVariable Long applicationId) {
        if (!isOfficerOrAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only Admin can preview draft certificate");
        }
        PetShopCertificateDraftDto dto = certificateService.getDraftCertificateDetails(applicationId);
        return new ResponseBuilder()
                .withData(dto)
                .build();
    }

    /**
     * Admin Preview Modal: Preview Draft Certificate PDF (in-memory, no DB write)
     */
    @GetMapping("/draft-preview/{applicationId}")
    public ResponseEntity<byte[]> getDraftCertificatePreview(@PathVariable Long applicationId) {
        if (!isOfficerOrAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only Admin can preview draft certificate");
        }
        byte[] pdfBytes = certificateService.getDraftCertificatePdfBytes(applicationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("Pet_Shop_Certificate_Draft_Preview.pdf")
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
