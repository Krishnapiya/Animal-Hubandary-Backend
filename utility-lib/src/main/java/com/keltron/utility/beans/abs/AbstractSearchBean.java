package com.keltron.utility.beans.abs;

import java.io.Serializable;



import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSearchBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 4345452379407962110L;

	@Null
	@Min(0)
	protected Integer pageNo = 0;
	@NotNull
	@Min(1)
	protected Integer pageSize = 25;
	@JsonIgnore
	@NotNull
	protected Sort dataSort;
}
