package com.neo.back.docker.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.neo.back.docker.dto.CreateDockerDto;
import com.neo.back.docker.service.CreateDockerService;

import reactor.core.publisher.Mono;

@RestController
public class DockerCreateController {
    
    private final CreateDockerService dockerService;

    public DockerCreateController(CreateDockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostMapping("/api/create-container")
    public Mono<String> createAndStartContainer(@RequestBody CreateDockerDto config) {
        // server.properties 파일 내용 생성
        String propertiesContent = "game=" + config.getGame() + "\n" +
                "ramCapacity=" + config.getRamCapacity() + "\n" +
                "paymentSystem=" + config.getPaymentSystem() + "\n" +
                "time=" + config.getTime();

        System.out.println(propertiesContent);

        return dockerService.createContainer(config);
    }

}