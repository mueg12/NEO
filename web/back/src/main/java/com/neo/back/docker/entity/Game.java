package com.neo.back.docker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Game {
    @Id
    private String game;
    
    private String dockerImage;
}
