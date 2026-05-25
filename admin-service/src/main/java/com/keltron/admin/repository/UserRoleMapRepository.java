package com.keltron.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keltron.admin.entity.UserRoleMap;

public interface UserRoleMapRepository extends JpaRepository<UserRoleMap, Long> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from UserRoleMap m where m.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    List<UserRoleMap> findByUser_Id(Long userId);

    @Query("""
        select m from UserRoleMap m
        join fetch m.role
        left join fetch m.office
        where m.user.id = :userId
        """)
    List<UserRoleMap> findAllByUserIdWithRelations(@Param("userId") Long userId);

    @Query(value = """
            select ur.user_id,
              case
                when o.name is not null then rm.role_name || ' @ ' || o.name
                else rm.role_name
              end
            from master.user_roles ur
            join master.role_master rm on rm.id = ur.role_id
            left join master.office o on o.id = ur.office_id
            """, nativeQuery = true)
    List<Object[]> findAllUserRolePairs();
}
