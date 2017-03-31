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
	
	// has to be boxed long to allow for nulls
	private Long uaId;
	
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

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
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

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Long getUaId() {
		return uaId;
	}

	public void setUaId(Long uaId) {
		this.uaId = uaId;
	}

}
