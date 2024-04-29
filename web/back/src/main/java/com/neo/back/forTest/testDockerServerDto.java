package com.neo.back.forTest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class testDockerServerDto {
    Long id;
    String serverName;
    //String user;
    String edgeServer;
    int port;
    String dockerId;
    int ram;
    String game;

    public testDockerServerDto(Long id, Long id1, String serverName, String edgeServerName, int port, String dockerId, int ramCapacity, String game) {
        this.id = id;
        this.serverName = serverName;
        this.edgeServer = edgeServerName;
        this.port = port;
        this.dockerId = dockerId;
        this.ram = ramCapacity;
        this.game = game;
    }
}
