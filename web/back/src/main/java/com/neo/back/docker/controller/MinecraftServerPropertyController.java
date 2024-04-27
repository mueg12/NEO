package com.neo.back.docker.controller;

import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.service.GameServerPropertyService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;


@RestController
public class MinecraftServerPropertyController {

    private final GameServerPropertyService service;

    private final DockerController dockerController;

    public MinecraftServerPropertyController(GameServerPropertyService service, DockerController dockerController) {
        this.service = service;
        this.dockerController = dockerController;
    }

    public Mono<GameServerSetting> updateServerProperties(@RequestBody GameServerSetting properties) {
        // Save properties to the database
        GameServerSetting savedProperties = service.save(properties);

        // Assuming the properties need to be updated in a Docker container as well
        try {
            return dockerController.changeFileInContainer()
                    .thenReturn(savedProperties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
