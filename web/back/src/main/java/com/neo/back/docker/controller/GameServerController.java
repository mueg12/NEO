package com.neo.back.docker.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.neo.back.docker.dto.FileDataDto;
import com.neo.back.docker.dto.StartGameServerDto;
import com.neo.back.docker.dto.UserUploadDto;
import com.neo.back.docker.service.GameDataService;
import com.neo.back.docker.service.StartAndStopGameServerService;

import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
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