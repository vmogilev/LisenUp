package com.lisenup.web.portal;

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
        model.addAttribute("toId", toId);
        return "send";
    }
}
