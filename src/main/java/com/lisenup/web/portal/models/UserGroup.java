package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_groups_all")

public class UserGroup {

	public static final String CREATED_BY = "PORTAL_USERGROUP_CLASS";
	public static final String MODIFIED_BY = "PORTAL_USERGROUP_CLASS";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ugaId;

	@NotNull
	private long uaId;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@NotNull
	private String ugaName;

	@NotNull
	private String ugaDesc;

	@NotNull
	private String ugaSlug;
	
	@NotNull
	private boolean ugaActive;

	// default constructor needed for hibernate
	public UserGroup() { }

	public long getUgaId() {
		return ugaId;
	}

	public long getUaId() {
		return uaId;
	}

	public String getUgaName() {
		return ugaName;
	}

	public void setUgaName(String ugaName) {
		this.ugaName = ugaName;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUgaDesc() {
		return ugaDesc;
	}

	public void setUgaDesc(String ugaDesc) {
		this.ugaDesc = ugaDesc;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUgaSlug() {
		return ugaSlug;
	}

	public void setUgaSlug(String ugaSlug) {
		this.ugaSlug = ugaSlug;
		this.modifiedBy = MODIFIED_BY;
	}

	public boolean isUgaActive() {
		return ugaActive;
	}

	public void setUgaActive(boolean ugaActive) {
		this.ugaActive = ugaActive;
		this.modifiedBy = MODIFIED_BY;
	}

}
