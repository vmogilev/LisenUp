package com.lisenup.web.portal.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.TopicFeedback;
import com.lisenup.web.portal.models.TopicFeedbackRepository;
import com.lisenup.web.portal.models.User;
import com.lisenup.web.portal.models.UserRepository;
import com.lisenup.web.portal.utils.SessUtils;

@Controller
public class MyFeedbackController {
	
//	private Logger logger = LoggerFactory.getLogger(MyFeedbackController.class);
	
	private static final long ANON_USER_ID = 1;

	@Autowired
	private SessUtils sessUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TopicFeedbackRepository topicFeedbackRepository;

	@RequestMapping(
			value = "/myfeedback", 
			method = RequestMethod.GET)
	public String MyFeedback(
			@CookieValue(value = "lu", defaultValue = "") String anonCookie,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		AnonSession anonUser = sessUtils.getOrSetLuCookie(request, response, anonCookie);

		List<TopicFeedback> givenFeedback = null;
		User linkedUser = null;
		
		if ( anonUser.getUaId() != null ) {
			linkedUser = userRepository.findOne(anonUser.getUaId());
			givenFeedback = topicFeedbackRepository.findByUaIdOrderByCreatedAtDesc(anonUser.getUaId());
		} else {
			linkedUser = userRepository.findOne(ANON_USER_ID);
			givenFeedback = topicFeedbackRepository.findBySessIdOrderByCreatedAtDesc(anonUser.getSessId());
		}
		
//		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//		for ( TopicFeedback feedback : givenFeedback ) {
//			Date feedDate = feedback.getCreatedAt();
//			logger.info("date: " + df.format(feedDate));
//		}

		model.addAttribute("user", linkedUser);
		model.addAttribute("givenFeedback", givenFeedback);
		return "myfeedback";
	}

}
