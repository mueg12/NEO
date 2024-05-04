package com.neo.back.docker.controller;

import com.neo.back.docker.dto.GameServerSettingDto;
import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.service.GameServerPropertyService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@RestController
public class DockerController {

    @Autowired
    private GameServerPropertyService service;


    public DockerController() {

    }
    @PostMapping("/api/change-file")
    public Mono<String> changeFileInContainer(@RequestBody GameServerSettingDto req) throws IOException {

        System.out.println(req.toString());

        String dockerContainerId= req.getContainerId();
        Long UserId = req.getUserId();

        System.out.println(UserId);
        System.out.println(dockerContainerId);

         // 추후 jwt 토큰에 User 정보 담아둘 거임. 임시 코드

        GameServerSetting settings = service.loadSettings(UserId); // 서비스 메소드는 적절한 로직으로 구현되어야 함

        // 모든 필드와 값을 가져와서 "컬럼: 값" 형식의 문자열로 변환
        String content = service.getString(settings);

        System.out.println(content);

        // content 문자열을 바이트 배열로 변환
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // 파일 내용을 tar 파일로 압축
        byte[] tarFile = createTarContent(contentBytes);

        // tar 파일을 저장할 경로
        Path tarPath = Path.of("server.properties.tar");

        // tar 파일 바이트 배열을 실제 파일로 저장
        Files.write(tarPath, tarFile);

        // Docker API를 통해 파일을 컨테이너에 복사
        return service.ChangeFileinContainer(dockerContainerId, tarFile);

    }

    @GetMapping("/api/get-file")
    public Mono<String> getFileFromContainer() {
        String containerId = "87e5304723d4";
        String filePathInContainer = "/server/server.properties";

        // Docker 컨테이너로부터 파일 받아오기
        return service.getDockerContainerFile(containerId, filePathInContainer);
    }

    @GetMapping("/api/get-minecraftproperties")
    public Object GetMinecraftPropertiesFromContainer(){

        String tarPath = "server.properties.tar";

        try {
            String propertiesString  = service.extractPropertiesFromTar(tarPath);

            JSONObject json = new JSONObject();
            String[] lines = propertiesString .split("\n");

            for (String line : lines) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    String[] keyValue = line.split("=", 2);
                    if (keyValue.length == 2) {
                        json.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            return ResponseEntity.ok(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting server.properties";
        }
    }

    @PostMapping("/api/get-banlist")//특정 파일 읽어오는 용도 api
    public Mono<String> readAndConvertToJson(String containerId, String filePath) {
        String command = "cat " + filePath;
        return service.executeCommand(containerId, command)
                .map(content -> {
                    // 파일 내용을 JSON 객체로 변환
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    return json.toString();
                });
    }

    @GetMapping("/api/containers/{containerId}/stats")
    public Mono<String> getContainerStatsController(@PathVariable String containerId) {

        return service.getContainerStats(containerId);
    }

    private byte[] createTarContent(byte[] fileContent) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(out)) {

            // TarArchiveEntry 설정 (파일 이름은 임의로 지정)
            TarArchiveEntry entry = new TarArchiveEntry("server.properties");
            entry.setSize(fileContent.length); // 파일 크기 설정
            tarOut.putArchiveEntry(entry);

            // 파일 내용 쓰기
            tarOut.write(fileContent);
            tarOut.closeArchiveEntry();

            // tarOut을 닫아야 tar 파일 완성
            tarOut.finish();

            // 완성된 tar 파일의 바이트 배열 반환
            return out.toByteArray();
        }
    }

}

