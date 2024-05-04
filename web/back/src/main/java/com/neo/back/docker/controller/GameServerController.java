package com.neo.back.docker.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.dto.StartGameServerDto;
import com.neo.back.docker.service.GameDataService;
import com.neo.back.docker.service.StartAndStopGameServerService;

import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class GameServerController {

    private final GameDataService gameDataService;
    private final StartAndStopGameServerService startAndStopGameServerService;

    public GameServerController(GameDataService gameDataService,StartAndStopGameServerService startAndStopGameServerService) {
        this.gameDataService = gameDataService;
        this.startAndStopGameServerService = startAndStopGameServerService;
    }

    @GetMapping("/api/docker-file-list")
    public ResponseEntity<?> getDockerFileList(@RequestParam String path) {
        Mono<String> fileListInst = gameDataService.getFileAndFolderListInst(path);
        List<FileDataDto> fileList = gameDataService.getFileAndFolderList(fileListInst);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("api/server/start")
    public ResponseEntity<StartGameServerDto> serverStart() {
        StartGameServerDto startMes =  startAndStopGameServerService.getStartGameServer();
        System.out.println(startMes.getIsWorking());
        return ResponseEntity.ok(startMes);
    }

    @GetMapping("api/server/stop")
    public ResponseEntity<StartGameServerDto> serverStop() {
        StartGameServerDto stopMes =  startAndStopGameServerService.getStopGameServer();
        System.out.println(stopMes.getIsWorking());
        return ResponseEntity.ok(stopMes);
    }

}
