package com.neo.back.docker.entity;

import com.neo.back.springjwt.entity.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class DockerImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serverName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    private String imageId;

    private Long size;

    private String date;

    @ManyToOne
    @JoinColumn(name = "game")
    private Game game;

    @OneToOne
    @JoinColumn
    private GameServerSetting setting;
}