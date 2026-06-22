package com.keltron.dogbreeder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keltron.utility.jpa.entity.ApplicationStatusMaster;

public interface DogBreederApplicationStatusMasterRepository
        extends JpaRepository<ApplicationStatusMaster, Long> {

    Optional<ApplicationStatusMaster> findByStatusCode(String statusCode);
}