package com.lisenup.web.portal.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lisenup.web.portal.controllers.GetReplyController;
import com.lisenup.web.portal.models.EmailLog;
import com.lisenup.web.portal.models.EmailLogrepository;

@Service
public class MailService {

	private static final String CREATED_BY = "MAIL_SERVICE";
	
	private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailLogrepository emailLogrepository;
	
	private Logger logger = LoggerFactory.getLogger(GetReplyController.class);
	
	@Autowired
	public MailService(JavaMailSender javaMailSender){
		this.javaMailSender = javaMailSender;
	}
	
	@Async
	public void send(
			String to, String from, 
			String replyTo, String subject, 
			String body
		) {
		
		EmailLog emailLog = new EmailLog(CREATED_BY, to, replyTo, from, subject, body);
		
        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setReplyTo(replyTo);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(body);
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Error Sending Email to: " + to + " | ERROR: " + e.getMessage());
            emailLog.setElSent(false);
            emailLog.setElError(e.getMessage());
            emailLogrepository.save(emailLog);
        } finally {}

        try {
        	javaMailSender.send(mail);
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error("Error Sending Email to: " + to + " | ERROR: " + e.getMessage());
            emailLog.setElSent(false);
            emailLog.setElError(e.getMessage());
            emailLogrepository.save(emailLog);
        }
        
        emailLog.setElSent(true);
        emailLogrepository.save(emailLog);
	}

}
