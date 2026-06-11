package com.keltron.petshop.repository;

import org.springframework.stereotype.Repository;

import com.keltron.utility.jpa.entity.ApplicationWorkflow;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface ApplicationWorkflowRepository
        extends AbstractRepository<
                ApplicationWorkflow,
                Long> {

}