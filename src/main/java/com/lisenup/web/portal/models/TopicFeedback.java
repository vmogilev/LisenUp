package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "topic_feedback_all")
public class TopicFeedback {

	public static final String CREATED_BY = "PORTAL_TOPIC_FEEDBACK_CLASS";
	public static final String MODIFIED_BY = "PORTAL_TOPIC_FEEDBACK_CLASS";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long tfaId;
	
	@NotNull
	private long gtaId;
	
	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@NotNull
	private String tfaIpAddr;
	
	@NotNull
	private String tfaText;
	
	// default constructor needed for hibernate
	public TopicFeedback() { 
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
	}
	
	public TopicFeedback(long gtaId) {
		this.gtaId = gtaId;
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
	}
	
	public long getTfaId() {
		return tfaId;
	}

	public void setTfaId(long tfaId) {
		this.tfaId = tfaId;
		this.modifiedBy = MODIFIED_BY;
	}

	public long getGtaId() {
		return gtaId;
	}

	public void setGtaId(long gtaId) {
		this.gtaId = gtaId;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public String getTfaIpAddr() {
		return tfaIpAddr;
	}

	public void setTfaIpAddr(String tfaIpAddr) {
		this.tfaIpAddr = tfaIpAddr;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getTfaText() {
		return tfaText;
	}

	public void setTfaText(String tfaText) {
		this.tfaText = tfaText;
		this.modifiedBy = MODIFIED_BY;
	}
	
	
}
