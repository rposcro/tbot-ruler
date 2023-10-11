package com.tbot.ruler.controller.console;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConsoleErrorController {

    @GetMapping(path = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView error(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");
        return mav;
    }
}
