package com.lisenup.web.portal.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecwid.maleorang.MailchimpClient;
import com.ecwid.maleorang.MailchimpException;
import com.ecwid.maleorang.MailchimpObject;
import com.ecwid.maleorang.method.v3_0.lists.members.EditMemberMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.MemberInfo;
import com.lisenup.web.portal.models.GroupUsers;
import com.lisenup.web.portal.models.GroupUsersRepository;

@Service
public class MailchimpService {
	
	private Logger logger = LoggerFactory.getLogger(MailchimpService.class);

	@Autowired
	private GroupUsersRepository groupUsersRepository;

//	private final String apiKey, listId;
//	
//	public MailchimpService(String apiKey, String listId) {
//		this.apiKey = apiKey;
//		this.listId = listId;
//	}

	@Async
	public void sub(String apiKey, String listId, String email, String name, String ipAddress, GroupUsers groupUsers) {
		MailchimpClient client = new MailchimpClient(apiKey);
		MemberInfo member = null;
		StringWriter errors = new StringWriter();
		boolean failed = false;
		
        try {
            EditMemberMethod.CreateOrUpdate method = new EditMemberMethod.CreateOrUpdate(listId, email);
            method.status = "subscribed";
            method.ip_signup = ipAddress;
            method.email_type = "html";
            method.merge_fields = new MailchimpObject();
            method.merge_fields.mapping.put("NAME", name);

            // try for 3 times to hit mailchimp - it fails with timeouts sometimes
            for ( int retries = 0; retries < 3; retries++ ) {
            	try {
            		member = client.execute(method);
            		break;
				} catch (final java.net.SocketTimeoutException e) {
					logger.error("TIMEOUT: " + retries);
					logger.error(e.getMessage());
				}
            }
            
            logger.info("The user has been successfully subscribed: " + member);
            
        } catch (MailchimpException | IOException e) {
        	e.printStackTrace();
        	logger.error(e.getMessage());
        	e.printStackTrace(new PrintWriter(errors));
        	failed = true;
        } finally {
            try {
				client.close();
			} catch (IOException e) {
				failed = true;
				e.printStackTrace();
				logger.error(e.getMessage());
				e.printStackTrace(new PrintWriter(errors));
			}
        }

        // save the mailchimp data to the local user sub
        groupUsers.setGuaMailchimpId(member.id);
        groupUsers.setGuaMailchimpEmail(member.email_address);
        if ( failed ) {
        	groupUsers.setGuaMailchimpResponse(errors.toString());
        } else {
        	groupUsers.setGuaMailchimpResponse(member.unique_email_id);
        }
        groupUsersRepository.save(groupUsers);
	}

}
