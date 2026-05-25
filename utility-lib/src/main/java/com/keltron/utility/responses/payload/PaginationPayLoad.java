package com.keltron.utility.responses.payload;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Krishnapriya
 *
 * @param <T>
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationPayLoad<T> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5131331315853275475L;

	private Integer pageSize;

	private Integer pageNo;

	private Integer totalPages;

	private Long totalRecords;

	private List<T> content = new ArrayList<>();
}
