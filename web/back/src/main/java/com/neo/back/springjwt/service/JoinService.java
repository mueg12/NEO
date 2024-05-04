package com.neo.back.springjwt.service;

import com.neo.back.springjwt.dto.JoinDTO;
import com.neo.back.springjwt.entity.UserEntity;
import com.neo.back.springjwt.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){

        this.userRepository = userRepository;
       this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO){

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();



        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        UserEntity UserEntity = new UserEntity();

        UserEntity.setUsername(username);
        UserEntity.setPassword(bCryptPasswordEncoder.encode(password));
        UserEntity.setRole("ROLE_ADMIN");

        System.out.println(username);

        System.out.println(UserEntity);

        userRepository.save(UserEntity);

    }

}
