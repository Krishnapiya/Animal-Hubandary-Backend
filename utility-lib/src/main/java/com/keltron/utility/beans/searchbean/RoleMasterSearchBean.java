package com.keltron.utility.beans.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMasterSearchBean extends AbstractSearchBean{
	private long id;
	private static final long serialVersionUID = 1L;
	private String roleName;

	public RoleMasterSearchBean() {
		dataSort = Sort.by(Sort.Order.asc("id"));
	}

}