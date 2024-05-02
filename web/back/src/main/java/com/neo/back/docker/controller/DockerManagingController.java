package com.neo.back.docker.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
public class DockerManagingController {
    
    private final GetMyServerListService getServerService;
    private final CreateDockerService createDockerService;
    private final CloseDockerService closeDockerService;

    @GetMapping("/api/container/list")
    public List<MyServerListDto> getMyServerList() {

        return getServerService.getMyServerList();
    }

    @DeleteMapping("/api/container")
    public List<MyServerListDto> deleteContainer() {

        return getServerService.getMyServerList();
    }

    @PostMapping("/api/container/create")
    public Mono<String> createContainer(@RequestBody CreateDockerDto config) {

        return createDockerService.createContainer(config);
    }

    @PostMapping("/api/container/recreate")
    public Mono<String> recreateContainer(@RequestBody CreateDockerDto config) {

        return createDockerService.recreateContainer(config);
    }

    @PutMapping("/api/container/close")
    public Mono<String> closeContainer() {

        return closeDockerService.closeDockerService();
    }

}