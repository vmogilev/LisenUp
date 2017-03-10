package com.lisenup.web.portal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such group")
public class GroupNotFoundException extends RuntimeException {
	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -1790211652711971729L;

	public GroupNotFoundException(String group) {
		super(group + " not found");
	}

}
