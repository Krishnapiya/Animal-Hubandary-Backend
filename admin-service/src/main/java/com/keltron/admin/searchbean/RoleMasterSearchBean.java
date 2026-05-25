package com.keltron.admin.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMasterSearchBean extends AbstractSearchBean {

	private static final long serialVersionUID = 1L;
	private String roleName;
	private String sortBy = "id"; // Default sorting field
	private String sortOrder = "asc"; // Default sorting order

	public RoleMasterSearchBean() {
	}

	public Sort getSort() {
		return "desc".equalsIgnoreCase(sortOrder) ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
		super.setDataSort(getSort());
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
		super.setDataSort(getSort());
	}

}
