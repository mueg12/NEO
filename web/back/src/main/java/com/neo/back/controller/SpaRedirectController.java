package com.neo.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaRedirectController {

    @RequestMapping(value = "/**")
    public String redirect() {
        // Forward to home page so that route is preserved.
        return "forward:/index.html";
    }
}