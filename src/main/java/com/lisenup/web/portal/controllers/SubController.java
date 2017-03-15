package com.lisenup.web.portal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupUsers;
import com.lisenup.web.portal.models.GroupUsersRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.utils.HttpUtils;

@Controller
public class SubController {

	private static final String SUB_PREFIX = "$S-";

	private static final String CHANGE_PASSWORD = "change_me";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private GroupUsersRepository groupUsersRepository;

	
	@PostMapping("/sub")
	public String subComplete(
			@ModelAttribute User newUser,
			@RequestParam("orig_uaId") long origUaId,
			@RequestParam("orig_ugaId") long origUgaId,
			HttpServletRequest request, 
			Model model) {
		
		User user = userRepository.findOne(origUaId);
		UserGroup userGroup = userGroupRepository.findOne(origUgaId);
		
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
		if ( StringUtils.isEmpty(newUser.getUaFirstname()) ) {
			errors.add("Please enter your First Name");
		}
		if ( StringUtils.isEmpty(newUser.getUaLastname()) ) {
			errors.add("Please enter your Last Name");
		}

		// if there are any correctable errors send the sub back to form
		if ( !errors.isEmpty() ) {			
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("newuser", newUser);
			model.addAttribute("errors", errors);
			
			return "sub_form";
		}
		
		// if we got here, then all is good - lets start creating shit
		
		// first lets create a semi-random username for the new user
		// I just grab the last bit from UUID, add PREFIX and Group ID to It
		// for example:
		//    UUID = 067e6162-3b6f-4ae2-a171-2470b63dff00
		//    GROUP_ID = 2
		//    PREFIX = $S-
		//    UserName = $S-2-2470b63dff00
		String newUserName = UUID.randomUUID().toString();
		newUserName = newUserName.substring(newUserName.lastIndexOf('-'));
		newUser.setUaUsername(SUB_PREFIX + userGroup.getUgaId() + newUserName);
		
		// set the user's password
		newUser.setUaPassword(CHANGE_PASSWORD);
		
		// make the user inactive until email is confirmed
		newUser.setUaActive(false);
		
		// save user
		// TODO: Duplicate Email will fail with 
		//       constraint [ua_uk2]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
		userRepository.save(newUser);
		
		// update sub with the new user Id and where the sub came from
		GroupUsers newSub = new GroupUsers();
		newSub.setUaId(newUser.getUaId());
		newSub.setUgaId(userGroup.getUgaId());
		newSub.setGuaSubedAt(user.getFullName() + " / " + userGroup.getUgaName());
		newSub.setGuaIpAddr(HttpUtils.getIp(request));
		
		// make the sub inactive until email is confirmed
		newSub.setGuaActive(false);
		
		// finally save the sub too
		groupUsersRepository.save(newSub);

		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("newuser", newUser);

		return "sub_complete";
	}
	
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}/sub", 
			method = RequestMethod.GET)
	public String subForm(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug,
			Model model) {

		User user = findUser(toId);
		UserGroup userGroup = findGroup(groupSlug, user);
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("newuser", new User());
		
		return "sub_form";
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
