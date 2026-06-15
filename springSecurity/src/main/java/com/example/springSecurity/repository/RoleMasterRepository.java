package com.example.springSecurity.repository;

import java.util.Optional;

import com.keltron.utility.jpa.entity.RoleMaster;
import com.keltron.utility.jpa.repository.AbstractRepository;

public interface RoleMasterRepository extends AbstractRepository<RoleMaster, Integer> {

    Optional<RoleMaster> findByRoleNameIgnoreCase(String roleName);
}
