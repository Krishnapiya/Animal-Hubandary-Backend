package com.keltron.utility.web.controller.abs;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Krishnapriya
 *
 */
public abstract class AbstractController {

	@RequestMapping(method = RequestMethod.GET)
	public String apiInfo() {
		return "Welcome to " + getClass() + " services";
	}
}
