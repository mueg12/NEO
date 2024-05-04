package com.neo.back.springjwt.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // 유저네임 중복되지 않게 설정.
    private String username;
    private String name;
    private String password;

    private String email;
    private String role;
}