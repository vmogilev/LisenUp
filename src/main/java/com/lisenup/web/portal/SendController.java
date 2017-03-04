package com.lisenup.web.portal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SendController {

    @RequestMapping("/send")
    public String greeting(@RequestParam(value="to", required=true) String toName, Model model) {
        model.addAttribute("to", toName);
        return "send";
    }
}
