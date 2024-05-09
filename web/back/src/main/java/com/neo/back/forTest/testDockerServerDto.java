package com.neo.back.forTest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class testDockerServerDto {
    Long id;
    String serverName;
    String userName;
    String edgeServer;
    int port;
    String dockerId;
    int ram;
    String game;
    String baseImage;

    public testDockerServerDto(Long id, String serverName, String userName, String edgeServerName, int port, String dockerId, int ramCapacity, String game, String baseImage) {
        this.id = id;
        this.serverName = serverName;
        this.userName = userName;
        this.edgeServer = edgeServerName;
        this.port = port;
        this.dockerId = dockerId;
        this.ram = ramCapacity;
        this.game = game;
        this.baseImage = baseImage;
    }
}
