package com.lisenup.web.portal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class RootController {

	@RequestMapping(method=RequestMethod.GET)
    public String index(Model model) {
        // model.addAttribute("name", name);
        return "index";
    }

	@GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

}
