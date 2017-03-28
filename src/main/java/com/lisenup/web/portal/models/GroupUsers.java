package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "group_users_all")
public class GroupUsers {

	public static final String CREATED_BY = "PORTAL_GROUP_USERS_CLASS";
	public static final String MODIFIED_BY = "PORTAL_GROUP_USERS_CLASS";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long guaId;
	
	@NotNull
	private long ugaId;
	
	@NotNull
	private long uaId;

	@Version
	private Integer version;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@NotNull
	private boolean guaActive;
	
	@NotNull
	private String guaSubedAt;
	
	@NotNull
	private String guaIpAddr;
	
	private String guaMailchimpId;
	
	private String guaMailchimpEmail;
	
	private String guaMailchimpResponse;
	
	// Default for hibernate
	public GroupUsers() {
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
		this.guaActive = true;
	}
		
	public long getGuaId() {
		return guaId;
	}

	public long getUgaId() {
		return ugaId;
	}

	public void setUgaId(long ugaId) {
		this.ugaId = ugaId;
		this.modifiedBy = MODIFIED_BY;
	}

	public long getUaId() {
		return uaId;
	}

	public void setUaId(long uaId) {
		this.uaId = uaId;
		this.modifiedBy = MODIFIED_BY;
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

	public boolean isGuaActive() {
		return guaActive;
	}

	public void setGuaActive(boolean guaActive) {
		this.guaActive = guaActive;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGuaSubedAt() {
		return guaSubedAt;
	}

	public void setGuaSubedAt(String guaSubedAt) {
		this.guaSubedAt = guaSubedAt;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGuaIpAddr() {
		return guaIpAddr;
	}

	public void setGuaIpAddr(String guaIpAddr) {
		this.guaIpAddr = guaIpAddr;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGuaMailchimpId() {
		return guaMailchimpId;
	}

	public void setGuaMailchimpId(String guaMailchimpId) {
		this.guaMailchimpId = guaMailchimpId;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGuaMailchimpEmail() {
		return guaMailchimpEmail;
	}

	public void setGuaMailchimpEmail(String guaMailchimpEmail) {
		this.guaMailchimpEmail = guaMailchimpEmail;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGuaMailchimpResponse() {
		return guaMailchimpResponse;
	}

	public void setGuaMailchimpResponse(String guaMailchimpResponse) {
		this.guaMailchimpResponse = guaMailchimpResponse;
		this.modifiedBy = MODIFIED_BY;
	}
	
}
