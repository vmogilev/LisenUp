package com.lisenup.web.portal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.QuestionNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupQuestion;
import com.lisenup.web.portal.models.GroupQuestionRepository;
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
	private GroupQuestionRepository groupQuestionRepository;

	// RegEx only allows letters, numbers, '-' and '_'
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/{qId:[0-9]+}", 
			method = RequestMethod.GET)
	public String questionForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			@PathVariable("qId") Long qId,
			Model model) {
		
		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		GroupQuestion question = groupQuestionRepository.findByGqaIdAndGqaActive(qId, true);
		if (question == null) {
			throw new QuestionNotFoundException(qId);
		}

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("question", question);
		return "question_form";
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
		
		List<GroupQuestion> questions = groupQuestionRepository.findByUgaIdAndGqaActive(userGroup.getUgaId(), true);
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("questions", questions);
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
