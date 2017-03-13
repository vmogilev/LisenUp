package com.lisenup.web.portal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Feedback Is Empty")
public class EmptyFeedbackException extends RuntimeException {
	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -1740201452711971729L;

	public EmptyFeedbackException() {
		super("Feedback Is Empty");
	}

}
