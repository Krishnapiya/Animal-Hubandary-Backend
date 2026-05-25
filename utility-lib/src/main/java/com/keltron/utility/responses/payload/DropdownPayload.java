package com.keltron.utility.responses.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropdownPayload<T> implements Serializable {

	private static final long serialVersionUID = 2253986716843402176L;

	private T id;

	private String name;
}
