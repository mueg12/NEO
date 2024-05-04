package com.neo.back.springjwt.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
public class MainController {

    @GetMapping("/home")
    public String mainP(){

        //세션 정보 확인
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorites = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorites.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "home";
    }


}
