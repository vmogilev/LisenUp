package com.lisenup.web.portal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users_all")
public class User {

	public static final String CREATED_BY = "PORTAL";
	public static final String MODIFIED_BY = "PORTAL";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long uaId;

	@NotNull
	private String createdBy;

	@NotNull
	private String modifiedBy;

	@NotNull
	private String uaUsername;

	@NotNull
	private String uaFirstname;

	@NotNull
	private String uaLastname;

	@NotNull
	private String uaEmail;

	// default constructor needed for hibernate
	public User() { }

	public User(String username, String firstname, String lastname, String email) {
		this.createdBy = CREATED_BY;
		this.modifiedBy = MODIFIED_BY;
		this.uaUsername = username;
		this.uaFirstname = firstname;
		this.uaLastname = lastname;
		this.uaEmail = email;
	}

	public long getUaId() {
		return uaId;
	}

	public String getUaUsername() {
		return uaUsername;
	}

	public String getUaFirstname() {
		return uaFirstname;
	}

	public void setUaFirstname(String uaFirstname) {
		this.uaFirstname = uaFirstname;
	}

	public String getUaLastname() {
		return uaLastname;
	}

	public void setUaLastname(String uaLastname) {
		this.uaLastname = uaLastname;
	}

	public String getUaEmail() {
		return uaEmail;
	}

	public void setUaEmail(String uaEmail) {
		this.uaEmail = uaEmail;
	}

}
