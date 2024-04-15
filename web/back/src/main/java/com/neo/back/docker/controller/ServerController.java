package com.neo.back.docker.controller;


import com.neo.back.docker.service.createMineCraftConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/servers/create")
@ResponseBody
public class ServerController {

    private final createMineCraftConfig serverService;


    public ServerController(createMineCraftConfig serverService) {
        this.serverService = serverService;
    }

    @PostMapping
    public String createMineCraftconfig(){


        return "ok";
    }


}
