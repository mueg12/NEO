package com.neo.back.docker.controller;

import com.neo.back.docker.dto.GameServerSettingDto;
import com.neo.back.docker.service.GameServerSettingService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.dto.StartGameServerDto;
import com.neo.back.docker.service.GameDataService;
import com.neo.back.docker.service.StartAndStopGameServerService;
import com.neo.back.docker.service.UploadAndDownloadService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




@RestController
@RequiredArgsConstructor
public class GameServerController {

    private final GameDataService gameDataService;
    private final GameServerSettingService serverSettingService;
    private final StartAndStopGameServerService startAndStopGameServerService;
    private final UploadAndDownloadService uploadAndDownloadService;

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

    @GetMapping("/api/server/ListOfFileAndFolder")
    public ResponseEntity<List<FileDataDto>> getDockerFileList(@RequestParam String path) {
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

    @PostMapping("api/server/upload")
        public  ResponseEntity<String> uploadFile(MultipartFile[] files,@RequestParam String path) {
            Mono<String> Mes = uploadAndDownloadService.upload(files,path);
        return Mes.map(message -> ResponseEntity.ok().body("{\"uploadStatus\": \"" + message + "\"}")).block();
    }

    @PutMapping("api/server/delete")
    public ResponseEntity<Map<String, String>> putMethodName(@RequestParam String path) {
        Map<String, String> Mes = uploadAndDownloadService.deleteFileAndFolder(path);
        return ResponseEntity.ok(Mes);
    }
}
