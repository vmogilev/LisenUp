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
	
	@NotNull
	private String ipAddr;

	private String fullName;
	
	private String emailAddress;
	
	@NotNull
	private boolean emailVerified;
	
	// needed for hibernate
	public AnonSession() {
		this.createdBy = "SESSION";
		this.emailVerified = false;
	}

	public AnonSession(String sessId, String ipAddr) {
		this.createdBy = "SESSION";
		this.sessId = sessId;
		this.ipAddr = ipAddr;
		this.emailVerified = false;
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
