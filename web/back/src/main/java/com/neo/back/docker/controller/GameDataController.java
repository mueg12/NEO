package com.neo.back.docker.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neo.back.docker.dto.DockerListDto;
import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.service.GameDataService;

import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class GameDataController {

    private final GameDataService gameDataService;

    public GameDataController(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }
        
    @GetMapping("/api/userDockerList")
    public ResponseEntity<List<DockerListDto>> getUserDockerListInfo() {
        List<DockerListDto> user = gameDataService.DockerListInfo(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/api/docker-file-list")
    public ResponseEntity<?> getDockerFileList(@RequestParam Long Id, @RequestParam String path) {
        Mono<String> fileListInst = gameDataService.getFileAndFolderListInst(Id,path);
        List<FileDataDto> fileList = gameDataService.getFileAndFolderList(Id,fileListInst);
        return ResponseEntity.ok(fileList);
    }

}
