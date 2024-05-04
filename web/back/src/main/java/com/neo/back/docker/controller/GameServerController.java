package com.neo.back.docker.controller;

import com.neo.back.docker.dto.GameServerSettingDto;
import com.neo.back.docker.service.GameServerSettingService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.service.GameDataService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class GameServerController {

    private final GameDataService gameDataService;
    private final GameServerSettingService serverSettingService;

    @PutMapping("api/server/start")
    public Mono<String> serverStart() {
        return Mono.just("start success");
    }

    @PutMapping("api/server/stop")
    public Mono<String> serverStop() {
        return Mono.just("stop success");
    }

    @GetMapping("/api/docker-file-list")
    public ResponseEntity<?> getDockerFileList(@RequestParam String path) {
        Mono<String> fileListInst = gameDataService.getFileAndFolderListInst(path);
        List<FileDataDto> fileList = gameDataService.getFileAndFolderList(fileListInst);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/api/server/setting")
    public Mono<String> getServerSetting() {

        return serverSettingService.getServerSetting();
    }

    @PostMapping("/api/server/setting")
    public Mono<String> setServerSetting(@RequestBody GameServerSettingDto req) throws IOException {

        return serverSettingService.setServerSetting(req);
    }

    @PostMapping("/api/get-banlist")//특정 파일 읽어오는 용도 api
    public Mono<String> readAndConvertToJson(String containerId, String filePath) {
        String command = "cat " + filePath;
        return serverSettingService.executeCommand(containerId, command)
                .map(content -> {
                    // 파일 내용을 JSON 객체로 변환
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    return json.toString();
                });
    }

    @GetMapping("/api/containers/{containerId}/stats")
    public Mono<String> getContainerStatsController(@PathVariable String containerId) {

        return serverSettingService.getContainerStats(containerId);
    }

}
