package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "group_topics_all")
public class GroupTopic {

	public static final String CREATED_BY = "PORTAL_GROUP_TOPIC_CLASS";
	public static final String MODIFIED_BY = "PORTAL_GROUP_TOPIC_CLASS";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long gtaId;

	@NotNull
	private long ugaId;
	
	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@Version
	private Integer version;

	@NotNull
	private String gtaTitle;
	
	@NotNull
	private String gtaText;
	
	@NotNull
	private boolean gtaActive;
	
	@NotNull
	private int gtaOrder;

	// default constructor needed for hibernate
	public GroupTopic() { 
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
		this.gtaOrder = 0;
		this.gtaActive = true;
	}

	public long getGtaId() {
		return gtaId;
	}

	public long getUgaId() {
		return ugaId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public String getGtaTitle() {
		return gtaTitle;
	}

	public void setGtaTitle(String gtaTitle) {
		this.gtaTitle = gtaTitle;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGtaText() {
		return gtaText;
	}

	public void setGtaText(String gtaText) {
		this.gtaText = gtaText;
		this.modifiedBy = MODIFIED_BY;
	}

	public boolean isGtaActive() {
		return gtaActive;
	}

	public void setGtaActive(boolean gtaActive) {
		this.gtaActive = gtaActive;
		this.modifiedBy = MODIFIED_BY;
	}

	public int getGtaOrder() {
		return gtaOrder;
	}

	public void setGtaOrder(int gtaOrder) {
		this.gtaOrder = gtaOrder;
		this.modifiedBy = MODIFIED_BY;
	}
	
	
	
}
