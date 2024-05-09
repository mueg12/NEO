package com.neo.back.docker.controller;

import com.neo.back.docker.utility.GetCurrentUser;
import com.neo.back.springjwt.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.neo.back.docker.dto.CreateDockerDto;
import com.neo.back.docker.dto.MyServerListDto;
import com.neo.back.docker.service.CloseDockerService;
import com.neo.back.docker.service.CreateDockerService;
import com.neo.back.docker.service.UserServerListService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
public class DockerManagingController {
    
    private final UserServerListService userServerService;
    private final CreateDockerService createDockerService;
    private final CloseDockerService closeDockerService;
    private final GetCurrentUser getCurrentUser;

    @GetMapping("/api/container/list")
    public ResponseEntity<List<MyServerListDto>> getMyServerList() {
        User user = getCurrentUser.getUser();
        return userServerService.getServerList(user);
    }

    @DeleteMapping("/api/container/{ImageNum}")
    public Mono<String> deleteContainer(@PathVariable Long ImageNum) {

        return userServerService.deleteServer(ImageNum);
    }

    @PostMapping("/api/container/create")
    public Mono<String> createContainer(@RequestBody CreateDockerDto config) {
        User user = getCurrentUser.getUser();
        return createDockerService.createContainer(config, user);
    }

    @PostMapping("/api/container/recreate")
    public Mono<String> recreateContainer(@RequestBody CreateDockerDto config) {
        User user = getCurrentUser.getUser();
        return createDockerService.recreateContainer(config, user);
    }

    @PutMapping("/api/container/close")
    public Mono<String> closeContainer() {
        User user = getCurrentUser.getUser();
        return closeDockerService.closeDockerService(user);
    }

}