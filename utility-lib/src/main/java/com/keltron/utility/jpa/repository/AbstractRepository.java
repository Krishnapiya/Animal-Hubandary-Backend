package com.keltron.utility.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional(propagation = Propagation.MANDATORY)
public interface AbstractRepository<K, V>
		extends PagingAndSortingRepository<K, V>, JpaRepository<K, V>, JpaSpecificationExecutor<K> {

}
