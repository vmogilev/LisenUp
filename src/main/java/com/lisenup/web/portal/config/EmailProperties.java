package com.lisenup.web.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("lisenup.email")
public class EmailProperties {

	private String subPrefix = "$S";
	private String changePassword = "change_me";
	private String mailFrom = "admin@lisenup.com";
	private String replyTo = "admin@lisenup.com";
	private String mailSubject = "Please Confirm Your Subscription";
	private String subConfirmLink = "http://localhost:8080/subconf";
	
	public String getSubPrefix() {
		return subPrefix;
	}
	public void setSubPrefix(String subPrefix) {
		this.subPrefix = subPrefix;
	}
	public String getChangePassword() {
		return changePassword;
	}
	public void setChangePassword(String changePassword) {
		this.changePassword = changePassword;
	}
	public String getMailFrom() {
		return mailFrom;
	}
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}
	public String getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getSubConfirmLink() {
		return subConfirmLink;
	}
	public void setSubConfirmLink(String subConfirmLink) {
		this.subConfirmLink = subConfirmLink;
	}

	
}
