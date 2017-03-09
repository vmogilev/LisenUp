package com.lisenup.web.portal;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SendController {

	// regex filters out stuff like PC@Bddd
	@RequestMapping(value = "/{toId:[A-Za-z0-9\\-\\_]+}", method = RequestMethod.GET)
	public String greeting(@PathVariable("toId") String toId, Model model) {
		String email = "";

		User user = userRepository.findByUaUsername(toId);
		if (user != null) { 
			email = user.getUaEmail();
		} else {
			email = "Invalid username!";
		}

		model.addAttribute("toId", toId);
		model.addAttribute("email", email);
		return "send";
	}

	@Autowired
	private UserRepository userRepository;

}
