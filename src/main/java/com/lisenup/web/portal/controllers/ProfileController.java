package com.lisenup.web.portal.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.exceptions.UserNotFoundException;
import com.lisenup.web.portal.models.AnonFeedback;
import com.lisenup.web.portal.models.AnonFeedbackRepository;
import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.GroupTopic;
import com.lisenup.web.portal.models.GroupTopicRepository;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserGroup;
import com.lisenup.web.portal.models.UserGroupRepository;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.utils.SessUtils;

@Controller
public class ProfileController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private SessUtils sessUtils;

	@Autowired
	private GroupTopicRepository groupTopicRepository;

	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;
	
	@Autowired
	private AnonFeedbackRepository anonFeedbackRepository;

	/**
	 * userProfile - Prints user's Groups and builds links to view their topics
	 * 
	 * @param username
	 * @param anonCookie
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(
			value = "/{username:[A-Za-z0-9\\-\\_]+}", 
			method = RequestMethod.GET)
	public String userProfile(
			@PathVariable("username") String username,
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model
			) {
		
		User user = userRepository.findByUaUsername(username);
		
		if ( user == null ) {
			throw new UserNotFoundException("User: " + username + " is not found");
		}
		
		List<UserGroup> groups = userGroupRepository.findByUaIdAndUgaPublic(user.getUaId(), true); 
		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);

		List<Long> reviewedGroupIds = new ArrayList<>();
		for ( UserGroup group : groups ) {
			
			// build a list of available Topic Ids
			List<GroupTopic> topics = groupTopicRepository.findByUgaIdAndGtaActiveOrderByGtaOrderAsc(group.getUgaId(), true);
			List<Long> availFeedbackIds = new ArrayList<>();
			for ( GroupTopic topic : topics ) {
				if ( !availFeedbackIds.contains(topic.getGtaId()) ) {
					availFeedbackIds.add(topic.getGtaId());
				}
			}
			
			// get list of current feedbacks this anon user has given so far for this Group 
			List<AnonFeedback> anonFeedbacks = anonFeedbackRepository.findByUgaIdAndSessId(group.getUgaId(), anonUser.getSessId());
			
			// build a list of topic Ids that this anon user has ever given feedback to 
			// and that are part of the available topics list
			List<Long> givenFeedbackIds = new ArrayList<>();
			for ( AnonFeedback feedback : anonFeedbacks ) {
				if ( !givenFeedbackIds.contains(feedback.getGtaId()) && 
						availFeedbackIds.contains(feedback.getGtaId()) ) {
					givenFeedbackIds.add(feedback.getGtaId());
				}
			}
			
			// now that we have a count of feedback given topics and available topics
			// if the counts match - this group is fully reviewed
			if ( givenFeedbackIds.size() == topics.size() ) {
				reviewedGroupIds.add(group.getUgaId());
			}

		}
		
		model.addAttribute("user", user);
		model.addAttribute("groups", groups);
		model.addAttribute("reviewedGroupIds", reviewedGroupIds);
		
		return "user_profile";
	}
}
