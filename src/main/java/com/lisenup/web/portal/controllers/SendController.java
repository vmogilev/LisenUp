package com.lisenup.web.portal.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lisenup.web.portal.exceptions.EmptyFeedbackException;
import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.TopicNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;

@Controller
public class SendController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private GroupTopicRepository groupTopicRepository;
	
	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;
	
	@PostMapping("/feedback")
	public String feedbackSubmit(
			@ModelAttribute TopicFeedback feedback,
			@RequestParam("orig_uaId") long orig_uaId,
			@RequestParam("orig_ugaId") long orig_ugaId,
			@RequestParam("orig_gtaId") long orig_gtaId,
			HttpServletRequest request, 
			Model model) {
				
		if ( StringUtils.isEmpty(feedback.getTfaText()) ) {
			throw new EmptyFeedbackException();
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
		
		feedback.setTfaIpAddr(request.getRemoteAddr());
		topicFeedbackRepository.save(feedback);

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		model.addAttribute("feedback", feedback);

		return "feedback_submit";
		
	}

	// RegEx only allows letters, numbers, '-' and '_'
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{topicId:[0-9]+}", 
			method = RequestMethod.GET)
	public String topicForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("topicId") Long topicId,
			Model model) {
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		GroupTopic topic = groupTopicRepository.findByGtaIdAndGtaActive(topicId, true);
		if (topic == null) {
			throw new TopicNotFoundException(topicId);
		}

		model.addAttribute("feedback", new TopicFeedback(topicId));
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topic", topic);
		return "topic_form";
	}
	
	// RegEx only allows letters, numbers, '-' and '_'
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}", 
			method = RequestMethod.GET)
	public String userGroup(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug, 
			Model model) {
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		List<GroupTopic> topics = groupTopicRepository.findByUgaIdAndGtaActive(userGroup.getUgaId(), true);
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("topics", topics);
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
