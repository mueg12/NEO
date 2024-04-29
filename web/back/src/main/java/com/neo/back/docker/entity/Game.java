package com.neo.back.docker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Game {
    @Id
    private String game;

    private String dockerImage;

    @ManyToOne
    @JoinColumn
    private GameServerSetting defaultSetting; 
}
