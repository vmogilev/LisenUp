package com.lisenup.web.portal.flows;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.lisenup.web.portal.config.LisenUpProperties;
import com.lisenup.web.portal.controllers.GetReplyController;
import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.AnonSessionRepository;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupUsers;
import com.lisenup.web.portal.models.GroupUsersRepository;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.service.MailchimpService;
import com.lisenup.web.portal.utils.HttpUtils;

@Component
public class ShareNameFlow {
	
	private static final String TEMP_USER_CREATION = "TEMP_USER_CREATION";
	private static final long ANON_USER_ID = 1;
	
	private Logger logger = LoggerFactory.getLogger(GetReplyController.class);

//	private final LisenUpProperties.Email email;

	@Autowired
	private LisenUpProperties props;
		
//	@Autowired
//	private MailService mailer;
	
	@Autowired
	private MailchimpService mailchimp;
	
	@Autowired
	private UserRepository userRepository;
		
	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;
		
	@Autowired
	private GroupUsersRepository groupUsersRepository;
	
	@Autowired
	private AnonSessionRepository anonSessionRepository;

	@Autowired
	public ShareNameFlow(LisenUpProperties properties) {
		// see: "To work with @ConfigurationProperties beans you can just inject" section
		//      in: 
		//         https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
//		this.email = properties.getEmail();
	}

	/**
	 * save - Saves the Share Name Data to Database and Subs the User if so required
	 * 
	 * @param request
	 * @param user
	 * @param feedback
	 * @param topic
	 * @param group
	 * @param groupOwner
	 * @param anonUser
	 */
	public void save(
			HttpServletRequest request, 
			User user, 
			TopicFeedback feedback, 
			GroupTopic topic,
			UserGroup group, 
			User groupOwner, 
			AnonSession anonUser) {
		
		// update user's active flag if it was not set
		if ( !user.isUaActive() ) {
			userRepository.setActiveForUserId(true, TEMP_USER_CREATION, user.getVersion()+1, user.getUaId());
		}
		
		// update user's Name with the latest provided on the feedback
		if ( !user.getUaName().equals(feedback.getTfaReplyName()) ) {
			userRepository.setNameForUserId(feedback.getTfaReplyName(), TEMP_USER_CREATION, user.getVersion()+1, user.getUaId());
		}
		
		// only update Feedback's user from anonymous=1 to real user Id
		if ( feedback.getUaId() == ANON_USER_ID ) {
			topicFeedbackRepository.setRealUserForFeedbackId(user.getUaId(), TEMP_USER_CREATION, feedback.getVersion()+1, feedback.getTfaId());
		}
		
		// create a new sub if user agreed to it and it's enabled
		if ( feedback.isTfaAgreedToSub() && group.isUgaMailchimpEnabled() ) {
			GroupUsers newSub = new GroupUsers();
			newSub.setUgaId(group.getUgaId());  // group id
			newSub.setUaId(user.getUaId());     // subscriber user_id
			newSub.setGuaActive(true);
			newSub.setGuaSubedAt(groupOwner.getUaName() + " / " + group.getUgaName() + " / " + topic.getGtaTitle());
			newSub.setGuaIpAddr(HttpUtils.getIp(request));
			newSub.setCreatedBy("REPLY_CONF");
			
			// sub the user if not already
			boolean doMailchimp = subUser(newSub);
			
			if ( doMailchimp ) {
				
				// if we got here push the user to MailChimp (using Async call)
				
				// first figure out if it's out own list and pull global level API
				String apiKey = group.getUgaMailchimpApi();
				if ( apiKey == null || apiKey.equals("") ) {
					apiKey = props.getMailChimpApiKey();
				}
				
				mailchimp.sub(
						apiKey, group.getUgaMailchimpListId(), 
						user.getUaEmail(), feedback.getTfaReplyName(), 
						HttpUtils.getIp(request), newSub);
			}
		}
		
		if ( !anonUser.isEmailVerified() ) {
			// Save the name/email to Anon Session
			anonUser.setFullName(feedback.getTfaReplyName());
			anonUser.setEmailAddress(feedback.getTfaReplyEmail());
			anonUser.setEmailVerified(true);
			anonSessionRepository.save(anonUser);
		}
		
		// update Anon user's Name with the latest provided on the feedback
		if ( !anonUser.getFullName().equals(feedback.getTfaReplyName()) ) {
			anonSessionRepository.setFullNameForSessId(feedback.getTfaReplyName(), anonUser.getSessId());
		}
	}
	

	private boolean subUser(GroupUsers newSub) {
		boolean subOk = false;
		try {
			groupUsersRepository.save(newSub);
			subOk = true;
		} catch (DataIntegrityViolationException e) {
			//e.printStackTrace();
			logger.info("User already subbed! Ignoring DUP.");
			logger.info(e.getMessage());
		}
		
		return subOk;
		
	}

}