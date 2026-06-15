package com.example.springSecurity.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.AbstractRepository;


public interface AppUserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
	Optional<Users> findByEmail(String email);

	@Query(value = "SELECT COUNT(*) > 0 FROM master.users WHERE lower(email) = lower(:email)", nativeQuery = true)
	boolean existsByEmailIgnoreCase(@Param("email") String email);

	@Query(value = "SELECT id FROM master.role_master WHERE role_name = :roleName LIMIT 1", nativeQuery = true)
	Optional<Integer> findRoleIdByRoleName(@Param("roleName") String roleName);

	@Query(value = "select rm.role_name from master.users u join master.role_master rm on rm.id = u.role_id where u.username = :username", nativeQuery = true)
	Optional<String> findRoleNameByUsername(@Param("username") String username);

	@Query(value = """
			select distinct role_name
			from (
			    select rm.role_name
			    from master.users u
			    join master.role_master rm on rm.id = u.role_id
			    where u.username = :username
			    union all
			    select rm.role_name
			    from master.users u
			    join master.user_roles ur on ur.user_id = u.id
			    join master.role_master rm on rm.id = ur.role_id
			    where u.username = :username
			) roles
			""", nativeQuery = true)
	List<String> findRoleNamesByUsername(@Param("username") String username);
}