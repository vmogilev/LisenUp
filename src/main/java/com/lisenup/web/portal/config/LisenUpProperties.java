package com.lisenup.web.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("lisenup")
public class LisenUpProperties {

	// to modify maxFeedbackLength just set the following ENV VARS:
	// LISENUP_MAX_FEEDBACK_LENGTH
	private int maxFeedbackLength = 1024;
	private String mailChimpApiKey;
	private String mailChimpListId;
	private final Email email = new Email();
	
	public int getMaxFeedbackLength() {
		return maxFeedbackLength;
	}
	public void setMaxFeedbackLength(int maxFeedbackLength) {
		this.maxFeedbackLength = maxFeedbackLength;
	}
	public Email getEmail() {
		return email;
	}
	public String getMailChimpApiKey() {
		return mailChimpApiKey;
	}
	public void setMailChimpApiKey(String mailChimpApiKey) {
		this.mailChimpApiKey = mailChimpApiKey;
	}
	public String getMailChimpListId() {
		return mailChimpListId;
	}
	public void setMailChimpListId(String mailChimpListId) {
		this.mailChimpListId = mailChimpListId;
	}


	public static class Email {
		
		// to modify any of these just set the following ENV VARS:
		// 	LISENUP_EMAIL_SUB_CONFIRM_LINK
		//	LISENUP_EMAIL_REPLY_CONFIRM_LINK
		//	etc...
		private String subPrefix = "$S";
		private String changePassword = "change_me";
		private String mailFrom = "admin@lisenup.com";
		private String replyTo = "admin@lisenup.com";
		private String subSubject = "Please Confirm Your Subscription";
		private String replySubject = "Please Confirm Your Email";
		private String subConfirmLink = "http://localhost:8080/subconf";
		private String replyConfirmLink = "http://localhost:8080/replyconf";
		
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
		public String getSubSubject() {
			return subSubject;
		}
		public void setSubSubject(String subSubject) {
			this.subSubject = subSubject;
		}
		public String getReplySubject() {
			return replySubject;
		}
		public void setReplySubject(String replySubject) {
			this.replySubject = replySubject;
		}
		public String getSubConfirmLink() {
			return subConfirmLink;
		}
		public void setSubConfirmLink(String subConfirmLink) {
			this.subConfirmLink = subConfirmLink;
		}
		public String getReplyConfirmLink() {
			return replyConfirmLink;
		}
		public void setReplyConfirmLink(String replyConfirmLink) {
			this.replyConfirmLink = replyConfirmLink;
		}	
	}	
}
