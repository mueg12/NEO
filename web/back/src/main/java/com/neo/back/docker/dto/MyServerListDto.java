package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyServerListDto {
    Long imageNum;

    String gameName;
    String version;
    String serverName;
    String date;
    
    public MyServerListDto(Long imageNum, String gameName, String version, String serverName, String date) {
        this.imageNum = imageNum;
        this.gameName = gameName;
        this.version = version;
        this.serverName = serverName;
        this.date = date;
    }
}
