package com.keltron.utility.manage.service.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.annotations.ReadTransactional;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.constants.RestExceptionCodes;
import com.keltron.utility.constants.logging.LogState;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.jpa.repository.AbstractRepository;
import com.keltron.utility.logging.LogKeyValues;
import com.keltron.utility.logging.LogStringBuilder;
import com.keltron.utility.responses.RestException;
import com.keltron.utility.responses.payload.DropdownPayload;
import com.keltron.utility.responses.payload.PaginationPayLoad;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractJpaService<T extends AbstractDto, ID, R extends AbstractRepository, B extends AbstractEntity> {

	@Autowired
	protected R repository;

	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 *
	 * @param <K>
	 * @param id
	 * @return
	 */
	@ReadTransactional
	public Optional<B> findByID(ID id) {
		if (log.isDebugEnabled()) {
			log.debug(LogStringBuilder.getLoggerString(LogState.INPUT, getClass(), "findByID", "Querying by id",
					new LogKeyValues("id", id)));
		}
		Optional<B> optFindId = repository.findById(id);
		if (optFindId.isEmpty()) {
			throw new RestException(RestExceptionCodes.NOT_EXIST, id);
		}
		return optFindId;
	}

	/**
	 *
	 * @param <K>
	 * @param dto
	 * @return
	 */
	@WriteTransactional
	public B save(T dto) {
		
		if (log.isDebugEnabled()) {
			log.debug(LogStringBuilder.getLoggerString(LogState.INPUT, getClass(), "save", null,
					new LogKeyValues("dto", dto)));
		}
		B entity = dto.toEntity();
		if (log.isDebugEnabled()) {
			log.debug(LogStringBuilder.getLoggerString(LogState.INTERMEDIATE, getClass(), "save", "Converted to entity",
					new LogKeyValues("entity", entity)));
		}
		entity = (B) repository.save(entity);
		return entity;
		
	}

	/**
	 *
	 * @param <K>
	 * @param dto
	 * @param id
	 * @return
	 */
	@WriteTransactional
	public B update(ID id, T dto) {
		Optional<B> optionalValue = repository.findById(id);
		if (optionalValue.isPresent()) {
			B entity = optionalValue.get();
			entity.copyFromDTO(dto);
			entity = (B) repository.save(entity);
			return entity;
		}
		throw new RestException(RestExceptionCodes.NOT_EXIST, id);
	}



	/**
	 *
	 * @param <K>
	 * @param id
	 * @return
	 */
	@WriteTransactional
	public B delete(ID id) {
		Optional<B> optionalValue = repository.findById(id);
		if (optionalValue.isPresent()) {
			B entity = optionalValue.get();
			repository.delete(entity);
			return entity;
		}
		throw new RestException(RestExceptionCodes.NOT_EXIST, id);
	}

	/**
	 *
	 * @param <K>
	 * @param predicate
	 * @param sort
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public PaginationPayLoad<T> findByCriteria(Specification<B> predicate, Sort sort, int pageNumber, int pageSize) {
		long executionStartTime = System.currentTimeMillis();
		Page<AbstractEntity> resultPage = loadDataFromDatabase(predicate, sort, pageNumber, pageSize);
		long executionEndTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
					"Query Execution Time in Millis",
					new LogKeyValues("Execution Time Millis", (executionEndTime - executionStartTime)),
					new LogKeyValues("pageNumber", pageNumber), new LogKeyValues("pageSize", pageSize)));
		}
		PaginationPayLoad payLoad = new PaginationPayLoad();
		if (resultPage.hasContent()) {
			ArrayList<T> returnList = new ArrayList<>();
			executionStartTime = System.currentTimeMillis();
			for (AbstractEntity k : resultPage.getContent()) {
				returnList.add(k.toDTO());
			}
			executionEndTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
						"Result page to dto conversion time in Millis",
						new LogKeyValues("Conversion Time Millis", (executionEndTime - executionStartTime)),
						new LogKeyValues("pageNumber", pageNumber), new LogKeyValues("pageSize", pageSize)));
			}
			payLoad.setPageNo(pageNumber);
			payLoad.setPageSize(returnList.size());
			payLoad.setContent(returnList);
			payLoad.setTotalPages(resultPage.getTotalPages());
			payLoad.setTotalRecords(resultPage.getTotalElements());
		} else {
			payLoad.setPageNo(pageNumber);
			payLoad.setPageSize(0);
			payLoad.setTotalPages(0);
			payLoad.setTotalRecords(0L);
		}
		return payLoad;
	}

	/**
	 *
	 * @param <K>
	 * @param predicate
	 * @param sort
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<T> findAll(Specification predicate, Sort sort) {
		long executionStartTime = System.currentTimeMillis();
		List<AbstractEntity> resultList = loadDataFromDatabase(predicate, sort);
		long executionEndTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
					"Query Execution Time in Millis",
					new LogKeyValues("Execution Time Millis", (executionEndTime - executionStartTime))));
		}
		ArrayList<T> returnList = new ArrayList<>();
		if (ValidationUtils.isValid(resultList)) {
			executionStartTime = System.currentTimeMillis();
			for (AbstractEntity k : resultList) {
				returnList.add(k.toDTO());
			}
			executionEndTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
						"Result page to dto conversion time in Millis",
						new LogKeyValues("Conversion Time Millis", (executionEndTime - executionStartTime))));
			}
		}
		return returnList;
	}

	/**
	 *
	 * @param <K>
	 * @param predicate
	 * @param sort
	 * @param asDropdown
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public PaginationPayLoad<DropdownPayload<?>> findByCriteria(Specification<B> predicate, Sort sort,
			boolean asDropdown, int pageNumber, int pageSize) {
		long executionStartTime = System.currentTimeMillis();
		Page<AbstractEntity> resultPage = loadDataFromDatabase(predicate, sort, pageNumber, pageSize);
		long executionEndTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
					"Query Execution Time in Millis",
					new LogKeyValues("Execution Time Millis", (executionEndTime - executionStartTime)),
					new LogKeyValues("pageNumber", pageNumber), new LogKeyValues("pageSize", pageSize)));
		}
		PaginationPayLoad payLoad = new PaginationPayLoad();
		if (resultPage.hasContent()) {
			ArrayList returnList = new ArrayList<>();
			executionStartTime = System.currentTimeMillis();

			for (AbstractEntity k : resultPage.getContent()) {
				returnList.add(asDropdown ? k.toDropDownPayload() : k.toDTO());
			}

			executionEndTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria",
						"Result page to dto conversion time in Millis",
						new LogKeyValues("Conversion Time Millis", (executionEndTime - executionStartTime)),
						new LogKeyValues("pageNumber", pageNumber), new LogKeyValues("pageSize", pageSize)));
			}
			payLoad.setPageNo(pageNumber);
			payLoad.setPageSize(returnList.size());
			payLoad.setContent(returnList);
			payLoad.setTotalPages(resultPage.getTotalPages());
			payLoad.setTotalRecords(resultPage.getTotalElements());
		} else {
			payLoad.setPageNo(pageNumber);
			payLoad.setPageSize(0);
			payLoad.setTotalPages(0);
			payLoad.setTotalRecords(0L);
		}
		return payLoad;
	}

	/**
	 *
	 * @param <K>
	 * @param predicate
	 * @param sort
	 * @param asDropdown
	 * @param pageNumber
	 * @param pageSize
	 * @param columnName
	 * @param valueName
	 * @return
	 */
	@ReadTransactional
	public PaginationPayLoad<DropdownPayload> findByCriteria(Class className, String columnName, String valueName) {
		long executionStartTime = System.currentTimeMillis();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<B> root = cq.from(className);
		cq.select(root.get(columnName));
		Expression<String> filterKeyExp = root.get(columnName).as(String.class);
		filterKeyExp = cb.lower(filterKeyExp);
		Predicate dPredicate = cb.like(filterKeyExp, "%" + valueName.trim().toLowerCase() + "%");
		cq.where(dPredicate);
		List<String> combinationList = entityManager.createQuery(cq).getResultList();
		long executionEndTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria-filter",
					"Query Execution Time in Millis",
					new LogKeyValues("Execution Time Millis", (executionEndTime - executionStartTime))));
		}
		PaginationPayLoad payLoad = new PaginationPayLoad();
		if (!combinationList.isEmpty()) {
			ArrayList returnList = new ArrayList<>();
			executionStartTime = System.currentTimeMillis();
			for (Object name : combinationList) {
				DropdownPayload dropDownPayload = new DropdownPayload();
				dropDownPayload.setId(String.valueOf(name));
				dropDownPayload.setName(String.valueOf(name));
				returnList.add(dropDownPayload);
			}

			executionEndTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria-filter",
						"Result page to dto conversion time in Millis",
						new LogKeyValues("Conversion Time Millis", (executionEndTime - executionStartTime))));
			}
			payLoad.setPageSize(returnList.size());
			payLoad.setContent(returnList);
		} else {
			payLoad.setPageSize(0);
			payLoad.setTotalPages(0);
			payLoad.setTotalRecords(0L);
		}
		return payLoad;
	}

	/**
	 *
	 * @param <K>
	 * @param predicate
	 * @param sort
	 * @param asDropdown
	 * @param pageNumber
	 * @param pageSize
	 * @param columnName
	 * @param valueName
	 * @return
	 */
	@ReadTransactional
	public PaginationPayLoad<DropdownPayload> findByCriteria(Class className, Specification predicate,
			String columnName, String valueName) {
		long executionStartTime = System.currentTimeMillis();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<B> root = cq.from(className);
		cq.select(root.get(columnName));
		Expression<String> filterKeyExp = root.get(columnName).as(String.class);
		filterKeyExp = cb.lower(filterKeyExp);
		Predicate dPredicate = cb.like(filterKeyExp, "%" + valueName.trim().toLowerCase() + "%");
		if (predicate != null) {
			cq.where(cb.and(dPredicate, predicate.toPredicate(root, cq, cb)));
		} else {
			cq.where(dPredicate);
		}
		List<String> combinationList = entityManager.createQuery(cq).getResultList();
		long executionEndTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria-filter",
					"Query Execution Time in Millis",
					new LogKeyValues("Execution Time Millis", (executionEndTime - executionStartTime))));
		}
		PaginationPayLoad payLoad = new PaginationPayLoad();
		if (!combinationList.isEmpty()) {
			ArrayList returnList = new ArrayList<>();
			executionStartTime = System.currentTimeMillis();
			for (Object name : combinationList) {
				DropdownPayload dropDownPayload = new DropdownPayload();
				dropDownPayload.setId(String.valueOf(name));
				dropDownPayload.setName(String.valueOf(name));
				returnList.add(dropDownPayload);
			}

			executionEndTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info(LogStringBuilder.getLoggerString(LogState.NOTICE, getClass(), "findByCriteria-filter",
						"Result page to dto conversion time in Millis",
						new LogKeyValues("Conversion Time Millis", (executionEndTime - executionStartTime))));
			}
			payLoad.setPageSize(returnList.size());
			payLoad.setContent(returnList);
		} else {
			payLoad.setPageSize(0);
			payLoad.setTotalPages(0);
			payLoad.setTotalRecords(0L);
		}
		return payLoad;
	}

	/**
	 *
	 * @param predicate
	 * @param sort
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@ReadTransactional
	private Page<AbstractEntity> loadDataFromDatabase(Specification predicate, Sort sort, int pageNumber,
			int pageSize) {
		return repository.findAll(predicate, PageRequest.of(pageNumber, pageSize, sort));
	}

	/**
	 *
	 * @param predicate
	 * @param sort
	 * @return
	 */
	@ReadTransactional
	private List<AbstractEntity> loadDataFromDatabase(Specification predicate, Sort sort) {
		return sort != null ? repository.findAll(predicate, sort) : repository.findAll(predicate);
	}

	/**
	 *
	 * @param Entity class
	 * @return Integer
	 */
	@ReadTransactional
	public Integer findMaxId(B childEntity) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<? extends AbstractEntity> root = cq.from(childEntity.getClass());
		cq.select(cb.coalesce(cb.max(root.get("id")), 0));
		Integer max = entityManager.createQuery(cq).getSingleResult();
		return max + 1;
	}

	/**
	 *
	 * @param Entity class
	 * @param String value
	 * @param String key
	 * @return Boolean
	 */
	@ReadTransactional
	public Boolean uniqueCheck(B childEntity, String value, String key) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<? extends AbstractEntity> cq = cb.createQuery(childEntity.getClass());
			Root<? extends AbstractEntity> root = cq.from(childEntity.getClass());
	       Predicate uniquePredicate = cb.equal(cb.lower(root.get(key)), value.toLowerCase());
			cq.where(uniquePredicate);
			entityManager.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return true;
		}
		return false;
	}
}
