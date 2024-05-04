package com.neo.back.springjwt.controller;


import com.neo.back.springjwt.dto.JoinDTO;
import com.neo.back.springjwt.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {


    private final JoinService joinService;

    public JoinController(JoinService joinService){
        this.joinService = joinService;
    }


    @PostMapping("/api/join")
    public String joinProcess(@RequestBody JoinDTO joinDTO){

        System.out.println(joinDTO);
        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);

        return "ok";
    }

}
