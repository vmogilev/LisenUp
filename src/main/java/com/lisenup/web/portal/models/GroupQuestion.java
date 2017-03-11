package com.lisenup.web.portal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "group_questions_all")
public class GroupQuestion {

	public static final String CREATED_BY = "PORTAL_GROUP_QUESTION_CLASS";
	public static final String MODIFIED_BY = "PORTAL_GROUP_QUESTION_CLASS";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long gqaId;

	@NotNull
	private long ugaId;
	
	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@NotNull
	private String gqaTitle;
	
	@NotNull
	private String gqaText;
	
	@NotNull
	private boolean gqaActive;

	// default constructor needed for hibernate
	public GroupQuestion() { }

	public long getGqaId() {
		return gqaId;
	}

	public long getUgaId() {
		return ugaId;
	}

	public String getGqaTitle() {
		return gqaTitle;
	}

	public void setGqaTitle(String gqaTitle) {
		this.gqaTitle = gqaTitle;
		this.modifiedBy = MODIFIED_BY;
	}

	public String getGqaText() {
		return gqaText;
	}

	public void setGqaText(String gqaText) {
		this.gqaText = gqaText;
		this.modifiedBy = MODIFIED_BY;
	}

	public boolean isGqaActive() {
		return gqaActive;
	}

	public void setGqaActive(boolean gqaActive) {
		this.gqaActive = gqaActive;
		this.modifiedBy = MODIFIED_BY;
	}
	
}
