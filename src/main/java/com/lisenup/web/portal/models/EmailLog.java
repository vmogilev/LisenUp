package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "email_log")
public class EmailLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long elId;

	@NotNull
	private String createdBy;

	@NotNull
	private String elTo;
	
	@NotNull
	private String elReplyTo;

	@NotNull
	private String elFrom;
	
	@NotNull
	private String elSubject;
	
	@NotNull
	private String elBody;
	
	@NotNull
	private boolean elSent;
	
	private String elError;
	
	// default constructor needed for hibernate
	public EmailLog() {
		// TODO Auto-generated constructor stub
	}
	

	public EmailLog(String createdBy, String elTo, String elReplyTo, String elFrom, String elSubject, String elBody) {
		super();
		this.createdBy = createdBy;
		this.elTo = elTo;
		this.elReplyTo = elReplyTo;
		this.elFrom = elFrom;
		this.elSubject = elSubject;
		this.elBody = elBody;
	}


	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getElTo() {
		return elTo;
	}

	public void setElTo(String elTo) {
		this.elTo = elTo;
	}

	public String getElReplyTo() {
		return elReplyTo;
	}


	public void setElReplyTo(String elReplyTo) {
		this.elReplyTo = elReplyTo;
	}


	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public String getElSubject() {
		return elSubject;
	}

	public void setElSubject(String elSubject) {
		this.elSubject = elSubject;
	}

	public String getElBody() {
		return elBody;
	}

	public void setElBody(String elBody) {
		this.elBody = elBody;
	}

	public boolean isElSent() {
		return elSent;
	}

	public void setElSent(boolean elSent) {
		this.elSent = elSent;
	}

	public String getElError() {
		return elError;
	}

	public void setElError(String elError) {
		this.elError = elError;
	}

	public long getElId() {
		return elId;
	}

}
