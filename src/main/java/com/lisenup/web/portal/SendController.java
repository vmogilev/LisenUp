package com.lisenup.web.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.exceptions.GroupNotFoundException;
import com.lisenup.web.exceptions.UserNotFoundException;

@Controller
public class SendController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;

	// regex filters out stuff like PC@Bddd
	@RequestMapping(
			value = "/{toId:[A-Za-z0-9\\-\\_]+}/{groupSlug:[A-Za-z0-9\\-\\_]+}", 
			method = RequestMethod.GET)
	public String greeting(
			@PathVariable("toId") String toId, 
			@PathVariable("groupSlug") String groupSlug, 
			Model model) {
		
		User user = userRepository.findByUaUsername(toId);
		UserGroup userGroup = userGroupRepository.findByUgaSlug(groupSlug);
		
		if (user == null || !user.isUaActive()) {
			throw new UserNotFoundException(toId);
		}
		
		if (userGroup == null) {
			throw new GroupNotFoundException(groupSlug);
		}
		
		
		model.addAttribute("toId", toId);
		model.addAttribute("email", user.getUaEmail());
		model.addAttribute("groupName", userGroup.getUgaName());
		return "send";
	}
	
}
