package com.lisenup.web.portal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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

import com.lisenup.web.portal.config.EmailProperties;
import com.lisenup.web.portal.exceptions.GroupNotFoundException;
import com.lisenup.web.portal.exceptions.InvalidSubException;
import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.GroupUsers;
import com.lisenup.web.portal.models.GroupUsersRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.service.MailService;
import com.lisenup.web.portal.utils.HttpUtils;

@Controller
public class SubController {

	@Autowired
	private EmailProperties email;
			
	private Logger logger = LoggerFactory.getLogger(SubController.class);
	
	@Autowired
	private MailService mailer;
	//private MailUtils mailer;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private GroupUsersRepository groupUsersRepository;

	@GetMapping("/subconf")
	public String subConfirmed(
			@RequestParam("u") String uaUsername,
			@RequestParam("g") long guaId,
			Model model
			) {
		
		User user = userRepository.findByUaUsername(uaUsername);
		GroupUsers sub = groupUsersRepository.findOne(guaId);
		UserGroup group = userGroupRepository.findOne(sub.getUgaId());
		User groupOwner = userRepository.findOne(group.getUaId());
		
		if ( user.getUaId() != sub.getUaId() ) {
			throw new InvalidSubException("uaUsername=" + uaUsername + " guaId=" + Long.toString(guaId));
		}
		
		// update user's active flag if it was not set
		if ( !user.isUaActive() ) {
			userRepository.setActiveForUserId(true, "SUB_CONFIRMED", user.getUaId());
		}
		
		// update user's sub active flag if it was not set
		if ( !sub.isGuaActive() ) {
			groupUsersRepository.setActiveForSubId(true, "SUB_CONFIRMED", sub.getGuaId());
		}

		model.addAttribute("user", groupOwner);
		model.addAttribute("group", group);

		return "sub_confirmed";
	}
	
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
//		if ( StringUtils.isEmpty(newUser.getUaFirstname()) ) {
//			errors.add("Please enter your First Name");
//		}
//		if ( StringUtils.isEmpty(newUser.getUaLastname()) ) {
//			errors.add("Please enter your Last Name");
//		}

		// if there are any correctable errors send the sub back to form
		if ( !errors.isEmpty() ) {			
			model.addAttribute("user", user);
			model.addAttribute("group", userGroup);
			model.addAttribute("newuser", newUser);
			model.addAttribute("errors", errors);
			
			return "sub_form";
		}
		
		newUser = createOrFindUser(newUser);
		
		// update sub with the new user Id and where the sub came from
		GroupUsers newSub = new GroupUsers();
		newSub.setUaId(newUser.getUaId());
		newSub.setUgaId(userGroup.getUgaId());
		newSub.setGuaSubedAt(user.getFullName() + " / " + userGroup.getUgaName());
		newSub.setGuaIpAddr(HttpUtils.getIp(request));
		
		// make the sub inactive until email is confirmed
		newSub.setGuaActive(false);
		
		// check if the email is already sub'ed to this group
		if ( createOrFindSub(newSub) ) {
			model.addAttribute("error", true);
		} else {			 
			// NOTE: the auto generated username has a $ as a first char
			//       it has to be URL Encoded as %24
			try {
				mailer.send(newUser.getUaEmail(), 
						this.email.getMailFrom(), this.email.getReplyTo(), this.email.getMailSubject(), 
						"Please confirm your Subsription to " +
						user.getUaFirstname() + "'s " + userGroup.getUgaName() +
						" by clicking the following link: " +
						this.email.getSubConfirmLink() + 
						"?u=" + newUser.getUaUsername().replaceAll("\\$", "%24") + 
						"&g=" + newSub.getGuaId()
						);				
			} catch (Exception e) {
				logger.info("Error Sending Email: " + e.getMessage());
			}
		}
		
		model.addAttribute("user", user);
		model.addAttribute("group", userGroup);
		model.addAttribute("newuser", newUser);

		return "sub_complete";
	}

	private boolean createOrFindSub(GroupUsers newSub) {
		
		GroupUsers existingSub = groupUsersRepository.findByUgaIdAndUaId(newSub.getUgaId(), newSub.getUaId());
		if ( existingSub != null ) {
			return true;
		}
		
		// if we got here save the sub
		groupUsersRepository.save(newSub);
		return false;
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
		
		// set fake names
		newUser.setUaFirstname(this.email.getSubPrefix() + "-FirstName");
		newUser.setUaLastname(this.email.getSubPrefix() + "-LastName");
		
		// set the user's password
		newUser.setUaPassword(this.email.getChangePassword());
		
		// make the user inactive until email is confirmed
		newUser.setUaActive(false);
		
		// parse Gravatar email hash
		newUser.setUaGravatarHash(HttpUtils.getGravatarUrl(newUser.getUaEmail()));
		
		// save user
		// TODO: Duplicate Email will fail with 
		//       constraint [ua_uk2]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
		userRepository.save(newUser);
		return newUser;
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
