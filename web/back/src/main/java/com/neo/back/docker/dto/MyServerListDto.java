package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyServerListDto {
    Long dockerId;

    String gameName;
    String version;
    String serverName;
    String date;
    
    public MyServerListDto(Long dockerId, String gameName, String version, String serverName, String date) {
        this.dockerId = dockerId;
        this.gameName = gameName;
        this.version = version;
        this.serverName = serverName;
        this.date = date;
    }
}
