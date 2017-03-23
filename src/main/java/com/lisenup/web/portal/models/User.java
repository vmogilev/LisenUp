package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users_all")
public class User {

	public static final String CREATED_BY = "PORTAL_USER_CLASS";
	public static final String MODIFIED_BY = "PORTAL_USER_CLASS";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long uaId;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@Version
	private Integer version;

	@NotNull
	private String uaUsername;

	@NotNull
	private String uaName;

	@NotNull
	private String uaEmail;
	
	@NotNull
	private String uaPassword;
	
	@NotNull
	private boolean uaActive;
	
	@NotNull
	private String uaGravatarHash;

	// default constructor needed for hibernate
	public User() {
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
		this.uaActive = true;
	}
	
	public long getUaId() {
		return uaId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getVersion() {
		return version;
	}

	public String getUaUsername() {
		return uaUsername;
	}
	
	public void setUaUsername(String uaUsername) {
		this.uaUsername = uaUsername;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUaName() {
		return uaName;
	}

	public void setUaName(String uaName) {
		this.uaName = uaName;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUaEmail() {
		return uaEmail;
	}

	public void setUaEmail(String uaEmail) {
		this.uaEmail = uaEmail;
		this.modifiedBy = MODIFIED_BY;
	}

	
	public String getUaPassword() {
		return uaPassword;
	}

	public void setUaPassword(String uaPassword) {
		this.uaPassword = uaPassword;
		this.modifiedBy = MODIFIED_BY;
	}

	public boolean isUaActive() {
		return uaActive;
	}

	public void setUaActive(boolean uaActive) {
		this.uaActive = uaActive;
		this.modifiedBy = MODIFIED_BY;
	}
	
	public String getUaGravatarHash() {
		return uaGravatarHash;
	}

	public void setUaGravatarHash(String uaGravatarHash) {
		this.uaGravatarHash = uaGravatarHash;
		this.modifiedBy = MODIFIED_BY;
	}
	
}
