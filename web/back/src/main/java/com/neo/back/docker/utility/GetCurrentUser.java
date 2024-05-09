package com.neo.back.docker.utility;

import com.neo.back.springjwt.entity.User;
import com.neo.back.springjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCurrentUser {
    private final UserRepository userRepo;

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        return userRepo.findByUsername(currentUserName);
    }

}
