package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "anon_sessions_all")
public class AnonSession {
	
	@Id
	private String sessId;
	
	@NotNull
	private String createdBy;

	private String fullName;
	
	private String emailAddress;
	
	// needed for hibernate
	public AnonSession() {
		this.createdBy = "SESSION";
	}

	public AnonSession(String sessId) {
		this.createdBy = "SESSION";
		this.sessId = sessId;
	}

	public String getSessId() {
		return sessId;
	}

	public void setSessId(String sessId) {
		this.sessId = sessId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
}
