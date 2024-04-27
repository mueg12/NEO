package com.neo.back.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.neo.back.docker.dto.CreateDockerDto;
import com.neo.back.docker.dto.MyServerListDto;
import com.neo.back.docker.service.CloseDockerService;
import com.neo.back.docker.service.CreateDockerService;
import com.neo.back.docker.service.GetMyServerListService;

import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class DockerManagingController {
    
    private final GetMyServerListService getServerService;
    private final CreateDockerService createDockerService;
    private final CloseDockerService closeDockerService;

    public DockerManagingController(GetMyServerListService getServerService, CreateDockerService createDockerService, CloseDockerService closeDockerService) {
        this.getServerService = getServerService;
        this.createDockerService = createDockerService;
        this.closeDockerService = closeDockerService;
    }

    @GetMapping("/api/myServer/list")
    public List<MyServerListDto> getMyServerList() {

        return getServerService.getMyServerList();
    }

    @PostMapping("/api/container/create")
    public Mono<String> createContainer(@RequestBody CreateDockerDto config) {

        String propertiesContent = "game=" + config.getGame() + "\n" +
                "ramCapacity=" + config.getRamCapacity() + "\n" +
                "paymentSystem=" + config.getPaymentSystem() + "\n" +
                "time=" + config.getTime();

        System.out.println(propertiesContent);

        return createDockerService.createContainer(config);
    }

    @PostMapping("/api/container/recreate")
    public Mono<String> recreateContainer(@RequestBody CreateDockerDto config) {

        return createDockerService.createContainer(config);
    }

    @PutMapping("/api/container/close")
    public Mono<String> closeContainer() {

        return closeDockerService.closeDockerService();
    }

}