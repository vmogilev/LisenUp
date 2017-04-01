package com.lisenup.web.portal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
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
import com.lisenup.web.portal.flows.ShareNameFlow;
import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.AnonSessionRepository;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
import com.lisenup.web.portal.models.GroupUsers;
import com.lisenup.web.portal.models.GroupUsersRepository;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.service.MailService;
import com.lisenup.web.portal.utils.HttpUtils;
import com.lisenup.web.portal.utils.SessUtils;

/**
 * @author mve
 *
 */
@Controller
public class GetReplyController {

	private static final String TEMP_USER_CREATION = "TEMP_USER_CREATION";
	private static final long ANON_USER_ID = 1;
	
//	private Logger logger = LoggerFactory.getLogger(GetReplyController.class);

	private final LisenUpProperties.Email email;

//	@Autowired
//	private LisenUpProperties props;

	@Autowired
	private SessUtils sessUtils;

	@Autowired
	private MailService mailer;
	
//	@Autowired
//	private MailchimpService mailchimp;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
		
	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;
	
	@Autowired
	private GroupTopicRepository groupTopicRepository;
	
	@Autowired
	private GroupUsersRepository groupUsersRepository;
	
	@Autowired
	private AnonSessionRepository anonSessionRepository; 
	
	@Autowired
	private ShareNameFlow shareNameFlow;

	
	@Autowired
	public GetReplyController(LisenUpProperties properties) {
		// see: "To work with @ConfigurationProperties beans you can just inject" section
		//      in: 
		//         https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
		this.email = properties.getEmail();
	}

	/**
	 * replyConfirmed - Confirms Email on Reply Request for a Feedback (aka Share Your Name)
	 * 
	 * @param uaUsername
	 * @param tfaUuid
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/replyconf")
	public String replyConfirmed(
			@RequestParam("u") String uaUsername,
			@RequestParam("t") String tfaUuid,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
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

		// it's possible that user confirms the email at a later time
		// or from a different machine / browser and we have to set
		// their cookie value to what it was while they provided the feedback
		// so they can view it (getreply_confirmed creates a link to view it)
		AnonSession anonUser = sessUtils.getOrSetLuCookieHard(request, response, feedback.getSessId());

		// save the Share Name Flow updates to the DB
		shareNameFlow.save(request, user, feedback, topic, group, groupOwner, anonUser);
		
//		// update user's active flag if it was not set
//		if ( !user.isUaActive() ) {
//			userRepository.setActiveForUserId(true, TEMP_USER_CREATION, user.getVersion()+1, user.getUaId());
//		}
//		
//		// update user's Name with the latest provided on the feedback
//		if ( !user.getUaName().equals(feedback.getTfaReplyName()) ) {
//			userRepository.setNameForUserId(feedback.getTfaReplyName(), TEMP_USER_CREATION, user.getVersion()+1, user.getUaId());
//		}
//		
//		// only update Feedback's user from anonymous=1 to real user Id
//		if ( feedback.getUaId() == ANON_USER_ID ) {
//			topicFeedbackRepository.setRealUserForFeedbackId(user.getUaId(), TEMP_USER_CREATION, feedback.getVersion()+1, feedback.getTfaId());
//		}
//		
//		// create a new sub if user agreed to it and it's enabled
//		if ( feedback.isTfaAgreedToSub() && group.isUgaMailchimpEnabled() ) {
//			GroupUsers newSub = new GroupUsers();
//			newSub.setUgaId(group.getUgaId());  // group id
//			newSub.setUaId(user.getUaId());     // subscriber user_id
//			newSub.setGuaActive(true);
//			newSub.setGuaSubedAt(groupOwner.getUaName() + " / " + group.getUgaName() + " / " + topic.getGtaTitle());
//			newSub.setGuaIpAddr(HttpUtils.getIp(request));
//			newSub.setCreatedBy("REPLY_CONF");
//			
//			// sub the user if not already
//			boolean doMailchimp = subUser(newSub);
//			
//			if ( doMailchimp ) {
//				
//				// if we got here push the user to MailChimp (using Async call)
//				
//				// first figure out if it's out own list and pull global level API
//				String apiKey = group.getUgaMailchimpApi();
//				if ( apiKey == null || apiKey.equals("") ) {
//					apiKey = props.getMailChimpApiKey();
//				}
//				
//				mailchimp.sub(
//						apiKey, group.getUgaMailchimpListId(), 
//						user.getUaEmail(), feedback.getTfaReplyName(), 
//						HttpUtils.getIp(request), newSub);
//			}
//		}
//
//		
//		if ( !anonUser.isEmailVerified() ) {
//			// Save the name/email to Anon Session
//			anonUser.setFullName(feedback.getTfaReplyName());
//			anonUser.setEmailAddress(feedback.getTfaReplyEmail());
//			anonUser.setEmailVerified(true);
//			anonSessionRepository.save(anonUser);
//		}
//		
//		// update Anon user's Name with the latest provided on the feedback
//		if ( !anonUser.getFullName().equals(feedback.getTfaReplyName()) ) {
//			anonSessionRepository.setFullNameForSessId(feedback.getTfaReplyName(), anonUser.getSessId());
//		}

		
		model.addAttribute("user", groupOwner);
		model.addAttribute("group", group);
		model.addAttribute("topic", topic);
		model.addAttribute("anonUser", anonUser);

		return "getreply_confirmed";
	}
	

//	private boolean subUser(GroupUsers newSub) {
//		boolean subOk = false;
//		try {
//			groupUsersRepository.save(newSub);
//			subOk = true;
//		} catch (DataIntegrityViolationException e) {
//			//e.printStackTrace();
//			logger.info("User already subbed! Ignoring DUP.");
//			logger.info(e.getMessage());
//		}
//		
//		return subOk;
//		
//	}

	@PostMapping("/updateAnonEmail")
	public String updateAnonEmailPost(
			@ModelAttribute User newUser,
			@RequestParam("next") String next,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {
		
		// pull anon user
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);

		// First check if anon user's email is still not verified 
		if ( anonUser.isEmailVerified() ) {
			
			model.addAttribute("newUser", newUser);
			model.addAttribute("anonUser", anonUser);
			model.addAttribute("next", next);
			
			return "getreply_update_email_done";			
		}

		// FIRST: deal with hard errors that we'll fail on HARD
		User linkedUser = userRepository.findOne(anonUser.getUaId());

		// check to make sure Linked User is STILL NOT active
		// if it is - I have no idea how this could have happened - fail fast!
		if ( linkedUser != null && linkedUser.isUaActive() ) {
			throw new UserNotFoundException("Linked User Id is already active! " + Long.toString(anonUser.getUaId()));
		}

		// find the original topic on which we need to update the email address
		TopicFeedback firstFeedback = topicFeedbackRepository.findByTfaUuid(linkedUser.getUaFirstTfaUuid());
		
		// we don't even want to go anywhere unless feedback is still assigned
		// to ANON user - if it was already processed then we are done and 
		// this is most likely a hack ...
		if ( firstFeedback == null || firstFeedback.getUaId() != ANON_USER_ID ) {
			throw new FeedbackNotFoundException(linkedUser.getUaFirstTfaUuid());
		}

		
		
		// NEXT: check for correctable errors
		List<String> errors = new ArrayList<>();

		// Try to find/create a user by email address and catch any constraint validation errors
		// but first:
		// 1) put the linkedUser's Name on the newUser
		// 2) tag the new user's first feedback UUID
		newUser.setUaName(linkedUser.getUaName());
		newUser.setUaFirstTfaUuid(firstFeedback.getTfaUuid());
		try {
			newUser = createOrFindUser(newUser);
		} catch (ConstraintViolationException e) {
			for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
				errors.add(violation.getMessage());
			}
			model.addAttribute("linkedUser", newUser);
			model.addAttribute("anonUser", anonUser);
			model.addAttribute("next", next);
			model.addAttribute("errors", errors);
			
			return "getreply_update_email_form";			
		}
		
		
		// if IDs of the new user and linked users don't match
		// link AnonUser with new user's ID and delete the old user
		if ( newUser.getUaId() != linkedUser.getUaId() ) {
			anonSessionRepository.setUaIdForSessId(newUser.getUaId(), anonUser.getSessId());
			userRepository.delete(linkedUser.getUaId());
		}
		
		// update first feedback with newUser.email
		firstFeedback.setTfaReplyEmail(newUser.getUaEmail());
		topicFeedbackRepository.save(firstFeedback);

		
		// NOTE: the auto generated username has a $ as a first char
		//       it has to be URL Encoded as %24
		mailer.send(newUser.getUaEmail(), 
				this.email.getMailFrom(), this.email.getReplyTo(), this.email.getReplySubject(), 
				"Please confirm your email by clicking on the following link: " +
				this.email.getReplyConfirmLink() + 
				"?t=" + firstFeedback.getTfaUuid() +
				"&u=" + newUser.getUaUsername().replaceAll("\\$", "%24")
				);				

		model.addAttribute("newUser", newUser);
		model.addAttribute("anonUser", anonUser);
		model.addAttribute("next", next);
		
		return "getreply_update_email_done";

	}
	
	/**
	 * getReplyPost - Get Reply (aka Share Your Name) Form Processor
	 * 
	 * @param newUser
	 * @param origUaId
	 * @param origUgaId
	 * @param orig_tfaUuid
	 * @param terms
	 * @param subscribe
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@PostMapping("/getreply")
	public String getReplyPost(
			@ModelAttribute User newUser,
			@RequestParam("orig_uaId") long origUaId,
			@RequestParam("orig_ugaId") long origUgaId,
			@RequestParam("orig_tfaUuid") String orig_tfaUuid,
			@RequestParam("printSub") Boolean printSub,
			@RequestParam(name = "terms", defaultValue = "false", required = false) Boolean terms,
			@RequestParam(name = "subscribe", defaultValue = "false", required = false) Boolean subscribe,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {
		
		User user = userRepository.findOne(origUaId);
		UserGroup userGroup = userGroupRepository.findOne(origUgaId);
		TopicFeedback feedback = topicFeedbackRepository.findByTfaUuid(orig_tfaUuid);
		

		// we don't even want to go anywhere unless feedback is still assigned
		// to ANON user - if it was already processed then we are done and 
		// this is most likely a hack ...
		if ( feedback == null || feedback.getUaId() != ANON_USER_ID ) {
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
		
		// pull anon user
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);
		
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

		// if there are any correctable errors go back to form
		if ( !errors.isEmpty() ) {			
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("newuser", newUser);
			model.addAttribute("feedback", feedback);
			model.addAttribute("topic", topic);
			model.addAttribute("anonUser", anonUser);
			model.addAttribute("terms", terms);
			model.addAttribute("subscribe", subscribe);
			model.addAttribute("printSub", printSub);			
			model.addAttribute("errors", errors);
			
			return "getreply_form";
		}
		
		// Try to create a user and catch any constraint validation errors
		// but first:
		// 1) save the most recent name which we'll use on the feedback
		// 2) tag the new user's first feedback UUID
		String newUserUaName = newUser.getUaName();
		newUser.setUaFirstTfaUuid(feedback.getTfaUuid());
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
			model.addAttribute("anonUser", anonUser);
			model.addAttribute("terms", terms);
			model.addAttribute("subscribe", subscribe);
			model.addAttribute("printSub", printSub);
			model.addAttribute("errors", errors);
			return "getreply_form";
		}
		
		// update feedback with newUser data (NOT the FOUND USER)
		// we are doing this to make sure we capture the most recent Name
		// the user.uaName is updated on the confirmation cycle since
		// we have to first make sure the use owns this account/email address
		feedback.setTfaReplyEmail(newUser.getUaEmail());
		feedback.setTfaReplyName(newUserUaName);
		
		// only set agrred to Sub to 0/1 if Sub was printed on the form to choose from
		// and only if mailchimp sub is enabled at the group level - otherwise leave it at null
		if ( userGroup.isUgaMailchimpEnabled() && printSub ) {
			feedback.setTfaAgreedToSub(subscribe);
		}
		topicFeedbackRepository.save(feedback);
		
		
		// update AnonSession with the matched uaId - this is the indicator that AnonUser
		// has requested to share the name/email at least once and for us a way
		// to identify if the email was verified or not through user.uaFirstTfaUuid
		if ( anonUser.getUaId() == null ) {
			anonSessionRepository.setUaIdForSessId(newUser.getUaId(), anonUser.getSessId());
		}
		
		// Check if anon user has a validated email and if so process it right here instead of sending email validation requests
		if ( anonUser.isEmailVerified() ) {
			// save the Share Name Flow updates to the DB
			shareNameFlow.save(request, newUser, feedback, topic, userGroup, user, anonUser);
			
			// Return email validated view right here
			model.addAttribute("user", user); //groupOwner
			model.addAttribute("group", userGroup);
			model.addAttribute("topic", topic);
			model.addAttribute("anonUser", anonUser);

			return "getreply_confirmed";

		}
		
		// NOTE: the auto generated username has a $ as a first char
		//       it has to be URL Encoded as %24
		mailer.send(newUser.getUaEmail(), 
				this.email.getMailFrom(), this.email.getReplyTo(), this.email.getReplySubject(), 
				"Please confirm your email by clicking on the following link: " +
				this.email.getReplyConfirmLink() + 
				"?t=" + orig_tfaUuid +
				"&u=" + newUser.getUaUsername().replaceAll("\\$", "%24")
				);				

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("newuser", newUser);
		model.addAttribute("topic", topic);

		return "getreply_requested";
	}


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
	
	/**
	 * getReplyForm - Get Reply aka Share Your Name Form
	 * 
	 * @param toId
	 * @param groupSlug
	 * @param tfaUuid
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{tfaUuid:[A-Za-z0-9\\-]+}/getreply", 
			method = RequestMethod.GET)
	public String getReplyForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("tfaUuid") String tfaUuid,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		TopicFeedback feedback = topicFeedbackRepository.findByTfaUuid(tfaUuid);
		
		// we don't even want to go anywhere unless feedback is still assigned
		// to ANON user - if it was already processed then we are done and 
		// this is most likely a hack ...
		if ( feedback == null || feedback.getUaId() != ANON_USER_ID ) {
			throw new FeedbackNotFoundException(tfaUuid);
		}
		
		GroupTopic topic = groupTopicRepository.findOne(feedback.getGtaId());

		// pull anon user
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);

		// if anon user is already tied to a pending user and it's email is not verified 
		// we have to stop here and warn them to confirm email before doing anything
		if ( !anonUser.isEmailVerified() && anonUser.getUaId() != null ) {
			User linkedUser = userRepository.findOne(anonUser.getUaId());
			
			model.addAttribute("linkedUser", linkedUser);
			model.addAttribute("anonUser", anonUser);
			model.addAttribute("next", ("/" + toId + "/" + groupSlug + "/" + tfaUuid + "/getreply"));
			
			return "getreply_unvalidated_email";			
		}

		// Check if the user is already subbed to the list - if so disable the sub checkbox
		GroupUsers sub = null;
		boolean printSub = true;
		if ( anonUser.getUaId() != null ) {
			sub = groupUsersRepository.findByUgaIdAndUaId(userGroup.getUgaId(), anonUser.getUaId());
		}
		if ( sub != null ) {
			printSub = false;
		}
		
		
		// 1) we have to do this here because Spring doesn't support Thymeleaf's ELVIS Conditionals in forms
		//    see: http://forum.thymeleaf.org/Using-conditional-logic-in-th-object-td4028232.html
		//    on the getreply_form we simply make the email field as readonly when anonUser.isEmailVerified
		//
		// 2) we allow name changes but no email changes because email is tied to a username so we don't want
		//    to give anon users a feature to change their email address which is not given to real registered users 
		User newUser = new User();
		if ( anonUser.isEmailVerified() ) {
			newUser.setUaEmail(anonUser.getEmailAddress());
			newUser.setUaName(anonUser.getFullName());
		}
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		model.addAttribute("feedback", feedback);
		model.addAttribute("anonUser", anonUser);
		model.addAttribute("subscribe", true);
		model.addAttribute("printSub", printSub);
		model.addAttribute("newuser", newUser);
		
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
