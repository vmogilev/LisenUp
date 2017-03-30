package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "anon_feedback_all")
public class AnonFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long afaId;
	
	@NotNull
	private String sessId;
	
	@NotNull
	private String createdBy;

	private Long tfaId;
	
	private Long gtaId;
	
	private Long uaId;
	
	private Long ugaId;
	
	private String tfaUuid;

	// for hibernate
	public AnonFeedback() {
		this.createdBy = "SESSION";
	}
	
	public AnonFeedback(String sessId) {
		this.sessId = sessId;
		this.createdBy = "SESSION";
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

	public Long getTfaId() {
		return tfaId;
	}

	public void setTfaId(Long tfaId) {
		this.tfaId = tfaId;
	}

	public Long getGtaId() {
		return gtaId;
	}

	public void setGtaId(Long gtaId) {
		this.gtaId = gtaId;
	}

	public Long getUaId() {
		return uaId;
	}

	public void setUaId(Long uaId) {
		this.uaId = uaId;
	}

	public Long getUgaId() {
		return ugaId;
	}

	public void setUgaId(Long ugaId) {
		this.ugaId = ugaId;
	}

	public String getTfaUuid() {
		return tfaUuid;
	}

	public void setTfaUuid(String tfaUuid) {
		this.tfaUuid = tfaUuid;
	}	
	
}
