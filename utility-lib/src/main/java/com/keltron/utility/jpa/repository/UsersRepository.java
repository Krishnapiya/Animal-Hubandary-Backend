package com.keltron.utility.jpa.repository;

import java.util.Optional;


import org.springframework.stereotype.Repository;

import com.keltron.utility.jpa.entity.Users;
@Repository
public interface UsersRepository extends AbstractRepository<Users, Long> {
	Optional<Users> findByUsername(String username);

}
