package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
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
	private long uaId;
	
	@NotNull
	private String tfaUuid;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@Version
	private Integer version;

	@NotNull
	private String tfaIpAddr;
	
	@NotNull
	private String tfaText;
	
	private String tfaReplyName;
	
	private String tfaReplyEmail;
	
	private Boolean tfaAgreedToSub;
	
	// default constructor needed for hibernate
	public TopicFeedback() { 
		this.uaId = 1; // 1=anonymous
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
	}
	
	public TopicFeedback(long gtaId) {
		this.gtaId = gtaId;
		this.uaId = 1; // 1=anonymous
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
	}
	
	public long getTfaId() {
		return tfaId;
	}

	public long getGtaId() {
		return gtaId;
	}

	public void setGtaId(long gtaId) {
		this.gtaId = gtaId;
		this.modifiedBy = MODIFIED_BY;
	}

	public long getUaId() {
		return uaId;
	}

	public void setUaId(long uaId) {
		this.uaId = uaId;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getTfaUuid() {
		return tfaUuid;
	}

	public void setTfaUuid(String tfaUuid) {
		this.tfaUuid = tfaUuid;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public Integer getVersion() {
		return version;
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

	public String getTfaReplyName() {
		return tfaReplyName;
	}

	public void setTfaReplyName(String tfaReplyName) {
		this.tfaReplyName = tfaReplyName;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getTfaReplyEmail() {
		return tfaReplyEmail;
	}

	public void setTfaReplyEmail(String tfaReplyEmail) {
		this.tfaReplyEmail = tfaReplyEmail;
		this.modifiedBy = MODIFIED_BY;
	}

	public Boolean isTfaAgreedToSub() {
		return tfaAgreedToSub;
	}

	public void setTfaAgreedToSub(boolean tfaAgreedToSub) {
		this.tfaAgreedToSub = tfaAgreedToSub;
		this.modifiedBy = MODIFIED_BY;
	}

}
