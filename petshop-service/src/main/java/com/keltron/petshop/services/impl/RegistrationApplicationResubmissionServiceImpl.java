package com.keltron.petshop.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.keltron.petshop.dto.RegistrationApplicationResubmissionUploadDto;
import org.springframework.beans.factory.annotation.Autowired;

import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.repository.PetShopRegistrationApplicationRepository;
@Service
public class RegistrationApplicationResubmissionServiceImpl {
	@Autowired
	private PetShopRegistrationApplicationRepository applicationRepository;

	public RegistrationApplicationResubmissionUploadDto uploadDocument(
	        MultipartFile file,
	        Long applicationId)
	        throws IOException {

	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("Please select a file.");
	    }

	    PetShopRegistrationApplication application =
	            applicationRepository.findById(applicationId)
	                    .orElseThrow(() ->
	                            new RuntimeException("Application not found."));

	    String applicationNumber =
	            application.getApplicationNumber();

	    Path uploadPath =
	            Paths.get(
	                    System.getProperty("user.home"),
	                    "Documents",
	                    "uploads",
	                    "resubmission",
	                    applicationNumber);

	    if (Files.exists(uploadPath) && !Files.isDirectory(uploadPath)) {
	        throw new RuntimeException(
	                uploadPath + " exists but is not a directory.");
	    }

	    Files.createDirectories(uploadPath);

	    String originalFileName =
	            file.getOriginalFilename();

	    String storedFileName =
	            System.currentTimeMillis()
	                    + "_"
	                    + originalFileName;

	    Path destination =
	            uploadPath.resolve(storedFileName);

	    Files.copy(
	            file.getInputStream(),
	            destination,
	            StandardCopyOption.REPLACE_EXISTING);

	    RegistrationApplicationResubmissionUploadDto dto =
	            new RegistrationApplicationResubmissionUploadDto();

	    dto.setFileName(originalFileName);

	    dto.setFilePath(
	            "resubmission/"
	                    + applicationNumber
	                    + "/"
	                    + storedFileName);

	    return dto;
	}
	}