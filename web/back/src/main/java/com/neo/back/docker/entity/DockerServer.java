package com.neo.back.docker.entity;

import com.neo.back.springjwt.entity.User;

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
public class DockerServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "edgeServer_edgeServerName")
    private EdgeServer edgeServer;
    private int port;
    private String dockerId;

    private int RAMCapacity;

    @ManyToOne
    @JoinColumn(name = "game_game")
    private Game game;

    @OneToOne
    @JoinColumn
    private GameServerSetting setting;

}
