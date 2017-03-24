package com.lisenup.web.portal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;

@Controller
public class ProfileController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;

	@RequestMapping(
			value = "/{username:[A-Za-z0-9\\-\\_]+}", 
			method = RequestMethod.GET)
	public String userProfile(
			@PathVariable("username") String username,
			Model model
			) {
		
		User user = userRepository.findByUaUsername(username);
		
		if ( user == null ) {
			throw new UserNotFoundException("User: " + username + " is not found");
		}
		
		List<UserGroup> groups = userGroupRepository.findByUaIdAndUgaPublic(user.getUaId(), true); 
		
		model.addAttribute("user", user);
		model.addAttribute("groups", groups);
		
		return "user_profile";
	}
}
