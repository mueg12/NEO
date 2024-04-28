package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyServerListDto {
    Long dockerId;

    String game;
    String serverName;
    String date;
    
    public MyServerListDto(Long dockerId, String game, String serverName, String date) {
        this.dockerId = dockerId;
        this.game = game;
        this.serverName = serverName;
        this.date = date;
    }
}
