package com.lisenup.web.portal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such subscription")
public class InvalidSubException extends RuntimeException {
	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -7390211652711971729L;

	public InvalidSubException(String sub) {
		super(sub + " not a valid subscription");
	}

}
