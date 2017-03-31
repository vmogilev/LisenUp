package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_groups_all")
public class UserGroup {

	public static final String CREATED_BY = "PORTAL_USER_GROUP_CLASS";
	public static final String MODIFIED_BY = "PORTAL_USER_GROUP_CLASS";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ugaId;

	@NotNull
	private long uaId;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@Version
	private Integer version;

	@NotNull
	private String ugaName;

	@NotNull
	private String ugaDesc;

	@NotNull
	private String ugaSlug;
	
	@NotNull
	private boolean ugaActive;
	
	@NotNull
	private boolean ugaPublic;
	
	private String ugaMailchimpListId;
	
	private String ugaMailchimpListName;
	
	private String ugaMailchimpApi;
	
	private Boolean ugaMailchimpEnabled;

	// default constructor needed for hibernate
	public UserGroup() {
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
		this.ugaActive = true;
		this.ugaPublic = false;
	}

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

	public boolean isUgaPublic() {
		return ugaPublic;
	}

	public void setUgaPublic(boolean ugaPublic) {
		this.ugaPublic = ugaPublic;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUgaMailchimpListId() {
		return ugaMailchimpListId;
	}

	public void setUgaMailchimpListId(String ugaMailchimpListId) {
		this.ugaMailchimpListId = ugaMailchimpListId;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUgaMailchimpListName() {
		return ugaMailchimpListName;
	}

	public void setUgaMailchimpListName(String ugaMailchimpListName) {
		this.ugaMailchimpListName = ugaMailchimpListName;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getUgaMailchimpApi() {
		return ugaMailchimpApi;
	}

	public void setUgaMailchimpApi(String ugaMailchimpApi) {
		this.ugaMailchimpApi = ugaMailchimpApi;
		this.modifiedBy = MODIFIED_BY;
	}

	public Boolean isUgaMailchimpEnabled() {
		if ( ugaMailchimpEnabled == null ) {
			return false;
		}
		return ugaMailchimpEnabled;
	}

	public void setUgaMailchimpEnabled(boolean ugaMailchimpEnabled) {
		this.ugaMailchimpEnabled = ugaMailchimpEnabled;
		this.modifiedBy = MODIFIED_BY;
	}

}
