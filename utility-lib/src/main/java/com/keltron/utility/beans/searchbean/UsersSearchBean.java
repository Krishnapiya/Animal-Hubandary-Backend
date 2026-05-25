package com.keltron.utility.beans.searchbean;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersSearchBean extends AbstractSearchBean {

	private static final long serialVersionUID = 1L;

	private String fname;
	private String email;
	private String employeeCode;
	private String search;
	private String sortBy = "id"; // Default
	private String sortOrder = "asc"; // Default
	private Integer id;
	private List<Integer> roleId;
	private List<Integer> genderId;
	private String mobileNo;

	// ✅ Allow only actual fields from User entity
	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
		"id", "fname", "email", "employeeCode", "mobileNo"
	);

	public UsersSearchBean() {
		dataSort = Sort.by(Sort.Order.asc(sortBy));
	}

	public Sort getSort() {
		return "desc".equalsIgnoreCase(sortOrder)
			? Sort.by(Sort.Order.desc(sortBy))
			: Sort.by(Sort.Order.asc(sortBy));
	}

	public void setSortBy(String sortBy) {
		if (ALLOWED_SORT_FIELDS.contains(sortBy)) {
			this.sortBy = sortBy;
		} else {
			this.sortBy = "id"; // fallback to id
		}
		super.setDataSort(getSort());
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
		super.setDataSort(getSort());
	}
}
