package com.lisenup.web.portal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lisenup.web.portal.config.LisenUpProperties;

@Controller
@RequestMapping("/")
public class RootController {

	@Autowired
	private LisenUpProperties props;

	@RequestMapping(method=RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("props", props);
        return "index";
    }

	@GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

}
