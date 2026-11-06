package com.keltron.petshop.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keltron.petshop.dto.PetShopCertificateDto;
import com.keltron.petshop.dto.PetShopCertificateDraftDto;
import com.keltron.petshop.dto.PetShopRegistrationApplicationDto;
import com.keltron.petshop.dto.PetShopRegistrationViewDto;
import com.keltron.petshop.entity.PetShopCertificate;
import com.keltron.petshop.entity.PetShopCertificateSequence;
import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.repository.PetShopCertificateRepository;
import com.keltron.petshop.repository.PetShopCertificateSequenceRepository;
import com.keltron.petshop.repository.PetShopRegistrationApplicationRepository;
import com.keltron.petshop.services.PetShopCertificateService;
import com.keltron.petshop.services.impl.PetShopNotificationServiceImpl;
import com.keltron.utility.jpa.entity.District;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;

@Service
public class PetShopCertificateServiceImpl implements PetShopCertificateService {

    @Autowired
    private PetShopCertificateRepository certificateRepository;

    @Autowired
    private PetShopCertificateSequenceRepository sequenceRepository;

    @Autowired
    private PetShopRegistrationApplicationRepository applicationRepository;

    @Autowired
    private PetShopRegistrationApplicationServiceImpl applicationService;

    @Autowired
    private PetShopRegistrationPdfService pdfService;

    @Autowired
    private PetShopNotificationServiceImpl notificationService;

    @Autowired
    private UsersRepository usersRepository;

    @Value("${application.certificate.upload-dir:}")
    private String certificateUploadDir;

    @Override
    @Transactional
    public PetShopCertificateDto generateCertificate(Long applicationId, Long adminUserId) {
        PetShopRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Validate approval status
        String statusCode = application.getStatus() != null ? application.getStatus().getStatusCode() : "";
        if (!"APPLICATION_APPROVED".equalsIgnoreCase(statusCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Certificate can only be generated for approved applications. Current status: " + statusCode);
        }

        // Idempotency: Return existing certificate if already generated
        Optional<PetShopCertificate> existingCert = certificateRepository.findByApplication_Id(applicationId);
        if (existingCert.isPresent()) {
            return toDTO(existingCert.get());
        }

        District district = application.getDistrict();
        if (district == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application does not have a registered district");
        }

        String districtCode = resolveDistrictCode(district);
        int issueYear = LocalDate.now().getYear();
        LocalDate issueDate = LocalDate.now();

        // Concurrency-safe atomic serial number allocation using pessimistic locking
        int serialNumber = allocateNextSerial(districtCode, issueYear);
        String certNumber = String.format("KL-%s-%d-PSL-%03d", districtCode, issueYear, serialNumber);

        // Generate Certificate PDF
        PetShopRegistrationViewDto viewDto = applicationService.getApplication(applicationId);
        byte[] pdfBytes = pdfService.generateCertificatePdf(certNumber, viewDto, issueDate);

        // Save PDF to permanent storage
        Path storageDir = resolveCertificateStorageDirectory();
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create certificate storage directory", e);
        }

        String fileName = "Pet_Shop_Certificate_" + certNumber + ".pdf";
        Path targetPath = storageDir.resolve(fileName);
        try {
            Files.write(targetPath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save certificate PDF to disk", e);
        }

        // Save certificate record
        PetShopCertificate certificate = new PetShopCertificate();
        certificate.setApplication(application);
        certificate.setCertificateNumber(certNumber);
        certificate.setDistrict(district);
        certificate.setDistrictCode(districtCode);
        certificate.setIssueYear(issueYear);
        certificate.setSerialNumber(serialNumber);
        certificate.setFilePath(targetPath.toAbsolutePath().toString());
        certificate.setFileName(fileName);
        certificate.setIssueDate(issueDate);
        certificate.setGeneratedAt(LocalDateTime.now());
        certificate.setGeneratedBy(adminUserId);
        certificate.setIsPublished(true);

        PetShopCertificate saved = certificateRepository.save(certificate);

        // Create notification for applicant
        try {
            if (application.getApplicantUserId() != null) {
                String appNum = application.getApplicationNumber() != null ? application.getApplicationNumber() : "";
                notificationService.createNotification(
                        application.getApplicantUserId(),
                        "PET_SHOP",
                        application.getId(),
                        "Certificate of Registration Issued",
                        "Your Pet Shop Certificate of Registration (" + certNumber + ") has been issued for application " + appNum + ".",
                        "INFO");
            }
        } catch (Exception ex) {
            System.err.println("Failed to create certificate notification: " + ex.getMessage());
        }

        return toDTO(saved);
    }

    private synchronized int allocateNextSerial(String districtCode, int issueYear) {
        Optional<PetShopCertificateSequence> seqOpt =
                sequenceRepository.findByDistrictCodeAndIssueYearForUpdate(districtCode, issueYear);

        PetShopCertificateSequence seq;
        if (seqOpt.isPresent()) {
            seq = seqOpt.get();
        } else {
            seq = new PetShopCertificateSequence();
            seq.setDistrictCode(districtCode);
            seq.setIssueYear(issueYear);
            seq.setLastSerialNumber(0);
            try {
                seq = sequenceRepository.saveAndFlush(seq);
            } catch (Exception e) {
                seq = sequenceRepository.findByDistrictCodeAndIssueYearForUpdate(districtCode, issueYear)
                        .orElseThrow(() -> new RuntimeException("Failed to obtain certificate sequence lock", e));
            }
        }

        int nextSerial = seq.getLastSerialNumber() + 1;
        seq.setLastSerialNumber(nextSerial);
        sequenceRepository.save(seq);
        return nextSerial;
    }

    public static String resolveDistrictCode(District district) {
        if (district == null) {
            return "01";
        }
        if (district.getId() != null && district.getId() >= 1 && district.getId() <= 14) {
            return String.format("%02d", district.getId());
        }
        String name = district.getName() != null ? district.getName().trim().toLowerCase() : "";
        switch (name) {
            case "thiruvananthapuram": return "01";
            case "kollam": return "02";
            case "pathanamthitta": return "03";
            case "alappuzha": return "04";
            case "kottayam": return "05";
            case "idukki": return "06";
            case "ernakulam": return "07";
            case "thrissur": return "08";
            case "palakkad": return "09";
            case "malappuram": return "10";
            case "kozhikode": return "11";
            case "wayanad": return "12";
            case "kannur": return "13";
            case "kasaragod": return "14";
            default: return "01";
        }
    }

    private Path resolveCertificateStorageDirectory() {
        if (certificateUploadDir != null && !certificateUploadDir.isBlank() && !certificateUploadDir.contains("/home/keltron")) {
            return Paths.get(certificateUploadDir);
        }
        return Paths.get(System.getProperty("user.home"), "Documents", "uploads", "certificates");
    }

    @Override
    @Transactional(readOnly = true)
    public PetShopCertificateDto getCertificateDetails(Long applicationId) {
        return certificateRepository.findByApplication_Id(applicationId)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PetShopCertificate getCertificateEntity(Long applicationId) {
        return certificateRepository.findByApplication_Id(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not yet generated"));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getCertificatePdfBytes(Long applicationId) {
        PetShopCertificate certificate = getCertificateEntity(applicationId);
        Path filePath = Paths.get(certificate.getFilePath());

        if (Files.exists(filePath)) {
            try {
                return Files.readAllBytes(filePath);
            } catch (IOException e) {
                System.err.println("Error reading stored certificate PDF: " + e.getMessage());
            }
        }

        // Fallback: regenerate if stored file was unexpectedly lost
        PetShopRegistrationViewDto viewDto = applicationService.getApplication(applicationId);
        byte[] pdfBytes = pdfService.generateCertificatePdf(
                certificate.getCertificateNumber(), viewDto, certificate.getIssueDate());

        // Rewrite to disk
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.write(filePath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ex) {
            System.err.println("Could not rewrite fallback certificate PDF: " + ex.getMessage());
        }

        return pdfBytes;
    }

    @Override
    @Transactional(readOnly = true)
    public void enrichApplicationsWithCertificate(List<PetShopRegistrationApplicationDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }

        List<Long> appIds = dtos.stream().map(PetShopRegistrationApplicationDto::getId).toList();
        List<PetShopCertificate> certs = certificateRepository.findByApplication_IdIn(appIds);

        Map<Long, PetShopCertificate> certMap = certs.stream()
                .collect(Collectors.toMap(c -> c.getApplication().getId(), c -> c, (a, b) -> a));

        for (PetShopRegistrationApplicationDto dto : dtos) {
            PetShopCertificate cert = certMap.get(dto.getId());
            if (cert != null) {
                dto.setCertificateNumber(cert.getCertificateNumber());
                dto.setHasCertificate(true);
                dto.setCertificateId(cert.getId());
            } else {
                dto.setHasCertificate(false);
            }
        }
    }

    private PetShopCertificateDto toDTO(PetShopCertificate cert) {
        PetShopCertificateDto dto = new PetShopCertificateDto();
        dto.setId(cert.getId());
        dto.setApplicationId(cert.getApplication().getId());
        dto.setApplicationNumber(cert.getApplication().getApplicationNumber());
        dto.setCertificateNumber(cert.getCertificateNumber());
        dto.setDistrictId(cert.getDistrict() != null ? cert.getDistrict().getId() : null);
        dto.setDistrictName(cert.getDistrict() != null ? cert.getDistrict().getName() : null);
        dto.setDistrictCode(cert.getDistrictCode());
        dto.setIssueYear(cert.getIssueYear());
        dto.setSerialNumber(cert.getSerialNumber());
        dto.setFileName(cert.getFileName());
        dto.setIssueDate(cert.getIssueDate());
        dto.setGeneratedAt(cert.getGeneratedAt());
        dto.setGeneratedBy(cert.getGeneratedBy());
        dto.setIsPublished(cert.getIsPublished());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String username = auth.getName();
        if (auth.getPrincipal() instanceof Jwt jwt && jwt.getSubject() != null) {
            username = jwt.getSubject();
        }
        if (username == null || username.isBlank() || "anonymousUser".equalsIgnoreCase(username)) {
            return null;
        }
        return usersRepository.findByUsername(username)
                .map(Users::getId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCertificateAccess(Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to access certificate");
        }

        boolean isOfficerOrAdmin = auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority().toUpperCase();
            return r.contains("ADMIN") || r.contains("OFFICER") || r.contains("CVO");
        });

        if (isOfficerOrAdmin) {
            return;
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to access certificate");
        }

        PetShopRegistrationApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (app.getApplicantUserId() == null || !app.getApplicantUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You do not own this application");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PetShopCertificateDraftDto getDraftCertificateDetails(Long applicationId) {
        PetShopRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        String statusCode = application.getStatus() != null ? application.getStatus().getStatusCode() : "";
        if (!"APPLICATION_APPROVED".equalsIgnoreCase(statusCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Certificate can only be previewed for approved applications. Current status: " + statusCode);
        }

        PetShopRegistrationViewDto viewDto = applicationService.getApplication(applicationId);
        PetShopCertificateDraftDto draft = new PetShopCertificateDraftDto();
        draft.setApplicationId(applicationId);
        draft.setApplicationNumber(application.getApplicationNumber());
        draft.setOwnerName(viewDto.getOwnerName());
        draft.setShopName(viewDto.getShopName());
        draft.setShopAddress(pdfService.formatShopLocation(viewDto));
        draft.setDistrict(viewDto.getDistrict());
        draft.setPermittedAnimals(pdfService.extractPermittedAnimals(viewDto.getAnimals()));
        draft.setIssueDate(LocalDate.now());

        Optional<PetShopCertificate> existingCert = certificateRepository.findByApplication_Id(applicationId);
        if (existingCert.isPresent()) {
            draft.setIsAlreadyGenerated(true);
            draft.setCertificateId(existingCert.get().getId());
            draft.setProposedCertificateNumber(existingCert.get().getCertificateNumber());
            if (existingCert.get().getIssueDate() != null) {
                draft.setIssueDate(existingCert.get().getIssueDate());
            }
        } else {
            draft.setIsAlreadyGenerated(false);
            District district = application.getDistrict();
            String districtCode = district != null ? resolveDistrictCode(district) : "01";
            int issueYear = LocalDate.now().getYear();
            int nextSerial = sequenceRepository.findByDistrictCodeAndIssueYear(districtCode, issueYear)
                    .map(seq -> seq.getLastSerialNumber() + 1)
                    .orElse(1);
            String proposedNumber = String.format("KL-%s-%d-PSL-%03d", districtCode, issueYear, nextSerial);
            draft.setProposedCertificateNumber(proposedNumber);
        }

        return draft;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getDraftCertificatePdfBytes(Long applicationId) {
        PetShopRegistrationApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        String statusCode = application.getStatus() != null ? application.getStatus().getStatusCode() : "";
        if (!"APPLICATION_APPROVED".equalsIgnoreCase(statusCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Certificate can only be previewed for approved applications. Current status: " + statusCode);
        }

        Optional<PetShopCertificate> existingCert = certificateRepository.findByApplication_Id(applicationId);
        if (existingCert.isPresent()) {
            return getCertificatePdfBytes(applicationId);
        }

        District district = application.getDistrict();
        String districtCode = district != null ? resolveDistrictCode(district) : "01";
        int issueYear = LocalDate.now().getYear();
        int nextSerial = sequenceRepository.findByDistrictCodeAndIssueYear(districtCode, issueYear)
                .map(seq -> seq.getLastSerialNumber() + 1)
                .orElse(1);
        String proposedNumber = String.format("KL-%s-%d-PSL-%03d", districtCode, issueYear, nextSerial);

        PetShopRegistrationViewDto viewDto = applicationService.getApplication(applicationId);
        return pdfService.generateCertificatePdf(proposedNumber, viewDto, LocalDate.now());
    }
}
