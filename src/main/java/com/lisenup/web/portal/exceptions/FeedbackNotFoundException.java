package com.lisenup.web.portal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such feedback")
public class FeedbackNotFoundException extends RuntimeException {

	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -6580221652711971719L;
	
	public FeedbackNotFoundException(String fuuid) {
		super(fuuid + " not found");
	}

}
