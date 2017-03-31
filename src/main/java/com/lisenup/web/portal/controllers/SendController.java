package com.lisenup.web.portal.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lisenup.web.portal.config.LisenUpProperties;
import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.TopicNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.AnonFeedback;
import com.lisenup.web.portal.models.AnonFeedbackRepository;
import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.utils.HttpUtils;
import com.lisenup.web.portal.utils.SessUtils;

/**
 * @author mve
 *
 */
@Controller
public class SendController {

	private Logger logger = LoggerFactory.getLogger(SendController.class);
			
	@Autowired
	private LisenUpProperties props;
	
	@Autowired
	private SessUtils sessUtils;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private GroupTopicRepository groupTopicRepository;
	
	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;

//	@Autowired
//	private AnonSessionRepository anonSessionRepository;
	
	@Autowired
	private AnonFeedbackRepository anonFeedbackRepository;

	
	/**
	 * feedbackSubmit - receives call from feedback_form and saves Feedback
	 * 
	 * @param feedback
	 * @param orig_uaId
	 * @param orig_ugaId
	 * @param orig_gtaId
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@PostMapping("/feedback")
	public String feedbackSubmit(
			@ModelAttribute TopicFeedback feedback,
			@RequestParam("orig_uaId") long orig_uaId,
			@RequestParam("orig_ugaId") long orig_ugaId,
			@RequestParam("orig_gtaId") long orig_gtaId,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model) {

		// check for correctable errors
		List<String> errors = new ArrayList<>();
		if ( StringUtils.isEmpty(feedback.getTfaText()) ) {
			errors.add("Please enter your Feedback");
		}

		if ( feedback.getTfaText().length() > props.getMaxFeedbackLength()+100 ) {
			errors.add("Sorry, your Feedback is longer than " + props.getMaxFeedbackLength() + " characters");
		}

		// we have to use findOne() and not check *Active attributes
		// to avoid null pointer exceptions ... the active flags are
		// checked below ... 
		GroupTopic topic = groupTopicRepository.findOne(feedback.getGtaId());
		UserGroup userGroup = userGroupRepository.findOne(topic.getUgaId());
		User user = userRepository.findOne(userGroup.getUaId());

		if ( orig_uaId != user.getUaId() || !user.isUaActive() ) {
			throw new UserNotFoundException(Long.toString(orig_uaId));
		}

		if ( orig_ugaId != userGroup.getUgaId() || !userGroup.isUgaActive() ) {
			throw new GroupNotFoundException(Long.toString(orig_ugaId));
		}

		if ( orig_gtaId != topic.getGtaId() || !topic.isGtaActive() ) {
			throw new TopicNotFoundException(orig_gtaId);
		}

		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);
		
		// if there are any correctable errors send the feedback back to form
		if ( !errors.isEmpty() ) {
			model.addAttribute("feedback", feedback);
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("topic", topic);
			model.addAttribute("errors", errors);
			model.addAttribute("props", props);
			model.addAttribute("anonUser", anonUser);
			return "feedback_form";
		}
		
		// if we got here save the topic - all good!
		feedback.setTfaIpAddr(HttpUtils.getIp(request));
		feedback.setTfaUuid(UUID.randomUUID().toString());
		feedback.setSessId(anonUser.getSessId());
		topicFeedbackRepository.save(feedback);
		
		// save the feedback in transient anon_feedback_all table
		AnonFeedback anonFeedback = new AnonFeedback(anonUser.getSessId());
		anonFeedback.setCreatedBy("feedbackSubmit");
		anonFeedback.setGtaId(feedback.getGtaId());
		anonFeedback.setTfaId(feedback.getTfaId());
		anonFeedback.setTfaUuid(feedback.getTfaUuid());
		anonFeedback.setUaId(feedback.getUaId());
		anonFeedback.setUgaId(userGroup.getUgaId());
		anonFeedbackRepository.save(anonFeedback);

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		model.addAttribute("feedback", feedback);
		model.addAttribute("anonUser", anonUser);

		return "feedback_submit";
		
	}

	 
	/**
	 * topicForm - Creates Topic Feedback Form
	 * 
	 * @param toId 			RegEx only allows letters, numbers, '-' and '_'
	 * @param groupSlug		RegEx only allows letters, numbers, '-' and '_'
	 * @param topicId		RegEx only allows numbers
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{topicId:[0-9]+}", 
			method = RequestMethod.GET)
	public String topicForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("topicId") Long topicId,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		GroupTopic topic = groupTopicRepository.findByGtaIdAndGtaActive(topicId, true);
		if (topic == null) {
			throw new TopicNotFoundException(topicId);
		}
		
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);
		List<TopicFeedback> givenFeedback = topicFeedbackRepository.findBySessIdAndGtaIdOrderByCreatedAtAsc(anonUser.getSessId(), topicId);
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for ( TopicFeedback feedback : givenFeedback ) {
			Date feedDate = feedback.getCreatedAt();
			logger.info("date: " + df.format(feedDate));
		}

		model.addAttribute("feedback", new TopicFeedback(topicId));
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		model.addAttribute("givenFeedback", givenFeedback);
		model.addAttribute("props", props);
		return "feedback_form";
	}
	 
	/**
	 * userGroup - Prints Group's Feedback Topics and builds links to them
	 * 
	 * @param toId			username - RegEx only allows letters, numbers, '-' and '_'
	 * @param groupSlug		RegEx only allows letters, numbers, '-' and '_'
	 * @param anonCookie
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}", 
			method = RequestMethod.GET)
	public String userGroup(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {
		
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie); 
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		List<GroupTopic> topics = groupTopicRepository.findByUgaIdAndGtaActiveOrderByGtaOrderAsc(userGroup.getUgaId(), true);
		
		// get list of current feedbacks this anon user has given so far for this Group 
		List<AnonFeedback> anonFeedbacks = anonFeedbackRepository.findByUgaIdAndSessId(userGroup.getUgaId(), anonUser.getSessId());
		
		// build a list of topic Ids that this anon user has ever given feedback to
		List<Long> givenFeedbackIds = new ArrayList<>();
		for ( AnonFeedback feedback : anonFeedbacks ) {
			if ( !givenFeedbackIds.contains(feedback.getGtaId()) ) {
				givenFeedbackIds.add(feedback.getGtaId());
			}
		}
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topics", topics);
		model.addAttribute("anonUser", anonUser);
		model.addAttribute("givenFeedbackIds", givenFeedbackIds);
		return "user_group";
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
