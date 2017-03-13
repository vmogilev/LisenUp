package com.lisenup.web.portal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such question")
public class TopicNotFoundException extends RuntimeException {

	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -1780221652711971719L;

	public TopicNotFoundException(long id) {
		super(id + " not found");
	}

}
