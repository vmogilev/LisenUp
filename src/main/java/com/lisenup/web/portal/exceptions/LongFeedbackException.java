package com.lisenup.web.portal.exceptions;

public class LongFeedbackException extends RuntimeException {
	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = -4740201452711971729L;

	public LongFeedbackException() {
		super("Feedback Is Too Long");
	}

}
