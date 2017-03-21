package com.lisenup.web.portal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/legal")
public class LegalController {
	
	@GetMapping("/privacy")
	public String index() {
		return "legal/privacy";
	}

}
