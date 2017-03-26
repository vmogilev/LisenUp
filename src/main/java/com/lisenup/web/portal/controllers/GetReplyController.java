package com.lisenup.web.portal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lisenup.web.portal.config.LisenUpProperties;
import com.lisenup.web.portal.exceptions.FeedbackNotFoundException;
import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.service.MailService;
import com.lisenup.web.portal.utils.HttpUtils;

@Controller
public class GetReplyController {

	private static final String TEMP_USER_CREATION = "TEMP_USER_CREATION";
	private static final long ANON_USER_ID = 1;

	private final LisenUpProperties.Email email;
				
	private Logger logger = LoggerFactory.getLogger(GetReplyController.class);
	
	@Autowired
	private MailService mailer;
	//private MailUtils mailer;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
		
	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;
	
	@Autowired
	private GroupTopicRepository groupTopicRepository;
	
	@Autowired
	public GetReplyController(LisenUpProperties properties) {
		// see: "To work with @ConfigurationProperties beans you can just inject" section
		//      in: 
		//         https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
		this.email = properties.getEmail();
	}

	@GetMapping("/replyconf")
	public String replyConfirmed(
			@RequestParam("u") String uaUsername,
			@RequestParam("t") String tfaUuid,
			Model model
			) {
		
		User user = userRepository.findByUaUsername(uaUsername);
		TopicFeedback feedback = topicFeedbackRepository.findByTfaUuid(tfaUuid);

		// Check if the Feedback UUID maps to real Feedback
		// if not - it's most likely a hack - fail fast and be nasty
		if ( feedback == null ) {
			throw new FeedbackNotFoundException(tfaUuid);
		}

		// see createOrFindUser(newUser) - we have to match on ModifiedBy if user is inactive
		// if not - it's most likely a hack - fail fast and be nasty
		if ( !user.isUaActive() && !user.getModifiedBy().equals(TEMP_USER_CREATION) ) {
			throw new UserNotFoundException("un: " + uaUsername + " mod: " + user.getModifiedBy());
		}

		GroupTopic topic = groupTopicRepository.findOne(feedback.getGtaId());
		UserGroup group = userGroupRepository.findOne(topic.getUgaId());
		User groupOwner = userRepository.findOne(group.getUaId());

		
		// update user's active flag if it was not set
		if ( !user.isUaActive() ) {
			userRepository.setActiveForUserId(true, TEMP_USER_CREATION, user.getVersion()+1, user.getUaId());
		}
		
		// only update Feedback's user from anonymous=1 to real user Id
		if ( feedback.getUaId() == ANON_USER_ID ) {
			topicFeedbackRepository.setRealUserForFeedbackId(user.getUaId(), TEMP_USER_CREATION, feedback.getVersion()+1, feedback.getTfaId());
		}

		model.addAttribute("user", groupOwner);
		model.addAttribute("group", group);
		model.addAttribute("topic", topic);

		return "getreply_confirmed";
	}
	
//	@GetMapping("/subconf")
//	public String subConfirmed(
//			@RequestParam("u") String uaUsername,
//			@RequestParam("g") long guaId,
//			Model model
//			) {
//		
//		User user = userRepository.findByUaUsername(uaUsername);
//		GroupUsers sub = groupUsersRepository.findOne(guaId);
//		UserGroup group = userGroupRepository.findOne(sub.getUgaId());
//		User groupOwner = userRepository.findOne(group.getUaId());
//		
//		if ( user.getUaId() != sub.getUaId() ) {
//			throw new InvalidSubException("uaUsername=" + uaUsername + " guaId=" + Long.toString(guaId));
//		}
//		
//		// update user's active flag if it was not set
//		if ( !user.isUaActive() ) {
//			userRepository.setActiveForUserId(true, "SUB_CONFIRMED", user.getVersion()+1, user.getUaId());
//		}
//		
//		// update user's sub active flag if it was not set
//		if ( !sub.isGuaActive() ) {
//			groupUsersRepository.setActiveForSubId(true, "SUB_CONFIRMED", sub.getGuaId());
//		}
//
//		model.addAttribute("user", groupOwner);
//		model.addAttribute("group", group);
//
//		return "sub_confirmed";
//	}
	
	@PostMapping("/getreply")
	public String getReplyPost(
			@ModelAttribute User newUser,
			@RequestParam("orig_uaId") long origUaId,
			@RequestParam("orig_ugaId") long origUgaId,
			@RequestParam("orig_tfaUuid") String orig_tfaUuid,
			@RequestParam(name = "terms", defaultValue = "false", required = false) Boolean terms,
			HttpServletRequest request, 
			Model model) {
		
		User user = userRepository.findOne(origUaId);
		UserGroup userGroup = userGroupRepository.findOne(origUgaId);
		TopicFeedback feedback = topicFeedbackRepository.findByTfaUuid(orig_tfaUuid);
		
		// check if the Feedback UUID exists
		if ( feedback == null ) {
			throw new FeedbackNotFoundException(orig_tfaUuid);
		}

		GroupTopic topic = groupTopicRepository.findOne(feedback.getGtaId());
		
		// check to make sure user is active
		if ( !user.isUaActive() ) {
			throw new UserNotFoundException(Long.toString(origUaId));
		}

		// check to make sure we are not being hacked/probed with ID scans
		if ( origUaId != userGroup.getUaId() || !userGroup.isUgaActive() ) {
			throw new GroupNotFoundException(Long.toString(origUgaId));
		}

		// check for correctable errors
		List<String> errors = new ArrayList<>();
		if ( StringUtils.isEmpty(newUser.getUaEmail()) ) {
			errors.add("Please enter your Email Address");
		}
		if ( StringUtils.isEmpty(newUser.getUaName()) ) {
			errors.add("Please enter your Name");
		}
		if ( !terms ) {
			errors.add("Please agree to reveal your name and email (check the box)");
		}

		// if there are any correctable errors send the sub back to form
		if ( !errors.isEmpty() ) {			
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("newuser", newUser);
			model.addAttribute("feedback", feedback);
			model.addAttribute("topic", topic);
			model.addAttribute("terms", terms);
			model.addAttribute("errors", errors);
			
			return "getreply_form";
		}
		
		// Try to create a user and catch any constraint validation errors
		try {
			newUser = createOrFindUser(newUser);
		} catch (ConstraintViolationException e) {
			for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
				errors.add(violation.getMessage());
			}
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("newuser", newUser);
			model.addAttribute("feedback", feedback);
			model.addAttribute("topic", topic);
			model.addAttribute("terms", terms);
			model.addAttribute("errors", errors);
			return "getreply_form";
		}
		
		
//		// update sub with the new user Id and where the sub came from
//		GroupUsers newSub = new GroupUsers();
//		newSub.setUaId(newUser.getUaId());
//		newSub.setUgaId(userGroup.getUgaId());
//		newSub.setGuaSubedAt(user.getUaName() + " / " + userGroup.getUgaName());
//		newSub.setGuaIpAddr(HttpUtils.getIp(request));
//		
//		// make the sub inactive until email is confirmed
//		newSub.setGuaActive(false);
//		
//		// check if the email is already sub'ed to this group
//		if ( createOrFindSub(newSub) ) {
//			model.addAttribute("error", true);
//		} else {			 
//			// NOTE: the auto generated username has a $ as a first char
//			//       it has to be URL Encoded as %24
//			try {
//				mailer.send(newUser.getUaEmail(), 
//						this.email.getMailFrom(), this.email.getReplyTo(), this.email.getSubSubject(), 
//						"Please confirm your Subsription to " +
//						user.getUaName() + "'s " + userGroup.getUgaName() +
//						" by clicking the following link: " +
//						this.email.getSubConfirmLink() + 
//						"?u=" + newUser.getUaUsername().replaceAll("\\$", "%24") + 
//						"&g=" + newSub.getGuaId()
//						);				
//			} catch (Exception e) {
//				logger.info("Error Sending Email: " + e.getMessage());
//			}
//		}


		// NOTE: the auto generated username has a $ as a first char
		//       it has to be URL Encoded as %24
		try {
			mailer.send(newUser.getUaEmail(), 
					this.email.getMailFrom(), this.email.getReplyTo(), this.email.getReplySubject(), 
					"Please confirm your email by clicking on the following link: " +
					this.email.getReplyConfirmLink() + 
					"?t=" + orig_tfaUuid +
					"&u=" + newUser.getUaUsername().replaceAll("\\$", "%24")
					);				
		} catch (Exception e) {
			logger.info("Error Sending Email to: " + newUser.getUaEmail() + " | ERROR: " + e.getMessage());
		}

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("newuser", newUser);
		model.addAttribute("topic", topic);

		return "getreply_requested";
	}

//	private boolean createOrFindSub(GroupUsers newSub) {
//		
//		GroupUsers existingSub = groupUsersRepository.findByUgaIdAndUaId(newSub.getUgaId(), newSub.getUaId());
//		if ( existingSub != null ) {
//			return true;
//		}
//		
//		// if we got here save the sub
//		groupUsersRepository.save(newSub);
//		return false;
//	}

	private User createOrFindUser(User newUser) {
		
		User foundUser = userRepository.findByUaEmail(newUser.getUaEmail()); 
		if ( foundUser != null ) {
			return foundUser;
		}
		
		// if we got here, then all is good - lets start creating shit
		
		// first lets create a semi-random username for the new user
		// I just grab the last bit from UUID, add PREFIX and Group ID to It
		// for example:
		//    UUID = 067e6162-3b6f-4ae2-a171-2470b63dff00
		//    PREFIX = $S
		//    UserName = $S-2470b63dff00
		String newUserName = UUID.randomUUID().toString();
		newUserName = newUserName.substring(newUserName.lastIndexOf('-'));
		newUser.setUaUsername(this.email.getSubPrefix() + newUserName);
		
		// set the user's password
		newUser.setUaPassword(this.email.getChangePassword());
		
		// make the user inactive until email is confirmed
		newUser.setUaActive(false);
		
		// parse Gravatar email hash
		newUser.setUaGravatarHash(HttpUtils.getGravatarUrl(newUser.getUaEmail()));

		// THIS HAS TO BE THE LAST STEP OR ModifiedBy will be replaced by the setters
		//
		// set created by to TEMP_USER_CREATION - we check for this on conf() side
		// to make sure we don't overwrite a newer update, for example if
		// user had this conf link in their mailbox for years and then
		// we axed their account and they decided to click the conf link
		// and would wipe our deactivation ...
		newUser.setCreatedBy(TEMP_USER_CREATION);
		newUser.setModifiedBy(TEMP_USER_CREATION);
		
		// save user
		// TODO: Duplicate Email will fail with 
		//       constraint [ua_uk2]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
		userRepository.save(newUser);
		return newUser;
	}
	
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{tfaUuid:[A-Za-z0-9\\-]+}/getreply", 
			method = RequestMethod.GET)
	public String subForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("tfaUuid") String tfaUuid,
			Model model) {

		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		TopicFeedback feedback = topicFeedbackRepository.findByTfaUuid(tfaUuid);
		
		// we don't even want to go anywhere unless feedback is still assigned
		// to ANON user - it it was already processed then we are done and 
		// this is most likely a hack ...
		if ( feedback == null || feedback.getUaId() != ANON_USER_ID ) {
			throw new FeedbackNotFoundException(tfaUuid);
		}
		
		GroupTopic topic = groupTopicRepository.findOne(feedback.getGtaId());
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		model.addAttribute("feedback", feedback);
		model.addAttribute("newuser", new User());
		
		return "getreply_form";
	}

	
	private UserGroup findGroup(String groupSlug, User user) {
		
		UserGroup userGroup = userGroupRepository.findByUaIdAndUgaSlug(user.getUaId(), groupSlug);
		if (userGroup == null || !userGroup.isUgaActive()) {
			throw new GroupNotFoundException(groupSlug);
		}
		return userGroup;
	
	}
	
	private User findUser(String toId) {

		User user = userRepository.findByUaUsername(toId);
		if (user == null || !user.isUaActive()) {
			throw new UserNotFoundException(toId);
		}
		return user;
		
	}

}
