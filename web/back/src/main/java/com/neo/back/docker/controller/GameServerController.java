package com.neo.back.docker.controller;

import com.neo.back.docker.dto.GameServerSettingDto;
import com.neo.back.docker.service.GameServerSettingService;
import com.neo.back.docker.utility.GetCurrentUser;
import com.neo.back.springjwt.entity.User;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.dto.StartGameServerDto;
import com.neo.back.docker.service.GameDataService;
import com.neo.back.docker.service.StartAndStopGameServerService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;



@RestController
@RequiredArgsConstructor
public class GameServerController {

    private final GameDataService gameDataService;
    private final GameServerSettingService serverSettingService;
    private final StartAndStopGameServerService startAndStopGameServerService;
    private final GetCurrentUser getCurrentUser;

    @GetMapping("/api/server/start")
    public ResponseEntity<StartGameServerDto> serverStart() {
        User user = getCurrentUser.getUser();
        StartGameServerDto startMes =  startAndStopGameServerService.getStartGameServer(user);
        System.out.println(startMes.getIsWorking());
        return ResponseEntity.ok(startMes);
    }

    @GetMapping("/api/server/stop")
    public ResponseEntity<StartGameServerDto> serverStop() {
        User user = getCurrentUser.getUser();
        StartGameServerDto stopMes =  startAndStopGameServerService.getStopGameServer(user);
        System.out.println(stopMes.getIsWorking());
        return ResponseEntity.ok(stopMes);
    }

    @GetMapping("/api/server/files")
    public ResponseEntity<?> getDockerFileList(@RequestParam String path) {
        User user = getCurrentUser.getUser();
        Mono<String> fileListInst = gameDataService.getFileAndFolderListInst(path, user);
        List<FileDataDto> fileList = gameDataService.getFileAndFolderList(fileListInst, user);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/api/server/setting")
    public Mono<String> getServerSetting() {
        User user = getCurrentUser.getUser();
        return serverSettingService.getServerSetting(user);
    }

    @PostMapping("/api/server/setting")
    public Mono<String> setServerSetting(@RequestBody GameServerSettingDto req) throws IOException {
        User user = getCurrentUser.getUser();
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
        public ResponseEntity<String> uploadFile(MultipartFile[] files) {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "uploadedFolder");
            tempDir.mkdirs();
            System.out.println("sam");
            System.out.println(files);
            System.out.println("sam");
            for (MultipartFile file : files) {
                try {
                    System.out.println("원래 경로: " + file.getOriginalFilename());
                    String originalFilename = file.getOriginalFilename();

                    // 파일의 경로에서 폴더 경로를 추출하여 폴더 생성
                    String directoryPath = originalFilename.substring(0, originalFilename.lastIndexOf(File.separator));
                    System.out.println(directoryPath);
                    if (directoryPath != null && !directoryPath.isEmpty()) {
                        File directory = new File(tempDir, directoryPath);
                        directory.mkdirs();
                    }

                    Path filePath = tempDir.toPath().resolve(file.getOriginalFilename());
                    Files.write(filePath, file.getBytes());
                    System.out.println(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }

        return ResponseEntity.ok("NULL");
    }
}
