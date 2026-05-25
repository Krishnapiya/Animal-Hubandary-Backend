package com.keltron.admin.repository;

import java.util.Optional;
import java.util.List;

import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.AbstractRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends AbstractRepository<Users, Long> {
	Optional<Users> findByUsername(String username);

	@Modifying
	@Query(value = "update master.users set role_id = :roleId where id = :userId", nativeQuery = true)
	void updatePrimaryRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);

	@Query(value = """
			select u.id as user_id, rm.role_name
			from master.users u
			left join master.role_master rm on rm.id = u.role_id
			""", nativeQuery = true)
	List<Object[]> findPrimaryRolePairs();
}