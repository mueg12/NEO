package com.neo.back.docker.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.service.GameDataService;

import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class GameServerController {

    private final GameDataService gameDataService;

    public GameServerController(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    @GetMapping("/api/docker-file-list")
    public ResponseEntity<?> getDockerFileList(@RequestParam String path) {
        Mono<String> fileListInst = gameDataService.getFileAndFolderListInst(path);
        List<FileDataDto> fileList = gameDataService.getFileAndFolderList(fileListInst);
        return ResponseEntity.ok(fileList);
    }

    @PutMapping("api/server/start")
    public Mono<String> serverStart() {
        return Mono.just("start success");
    }

    @PutMapping("api/server/stop")
    public Mono<String> serverStop() {
        return Mono.just("stop success");
    }

}
