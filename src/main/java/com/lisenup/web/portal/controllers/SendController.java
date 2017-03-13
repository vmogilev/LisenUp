package com.lisenup.web.portal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.TopicNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
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

	// RegEx only allows letters, numbers, '-' and '_'
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{tId:[0-9]+}", 
			method = RequestMethod.GET)
	public String topicForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("tId") Long tId,
			Model model) {
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		GroupTopic topic = groupTopicRepository.findByGtaIdAndGtaActive(tId, true);
		if (topic == null) {
			throw new TopicNotFoundException(tId);
		}

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
