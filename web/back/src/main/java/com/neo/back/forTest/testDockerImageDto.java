package com.neo.back.forTest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class testDockerImageDto {
    Long id;
    String serverName;
    //String user;
    String imageId;
    Long size;
    String date;
    String game;

    public testDockerImageDto(Long id, String serverName, String imageId, Long size, String date, String game) {
        this.id = id;
        this.serverName = serverName;
        //this.user = username;
        this.imageId = imageId;
        this.size = size;
        this.date = date;
        this.game = game;
    }
}
