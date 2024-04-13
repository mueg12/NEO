package com.neo.back.springjwt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/api/my")
    @ResponseBody
    public String myAPI() {

        return"my-route";
    }

}
