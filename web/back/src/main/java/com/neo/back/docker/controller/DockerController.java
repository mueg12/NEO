package com.neo.back.docker.controller;

import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.service.GameServerPropertyService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DockerController {

    private final WebClient dockerWebClient;

    @Autowired
    private GameServerPropertyService service;


    public DockerController(WebClient.Builder webClientBuilder) {
        this.dockerWebClient = webClientBuilder.baseUrl("http://외부ip:2375").
                filter(logRequestAndResponse()) // 로깅 필터를 여기에 추가
                .build();
    }

    @PostMapping("/api/start-container")
    public Mono<String> StartContainer() {

        String containerId = "0cb70cfdac2e945364482531e57f1fcfd81da84d1b90aa5486e9fd878131f04e";
        return dockerWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/containers/{containerId}/restart")
                        .queryParam("detachKeys", "ctrl-c") // 예시로 detachKeys를 ctrl-c로 설정
                        .build(containerId))
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn("Container started with ID: " + containerId);

    }

    @PostMapping("/api/stop-container")
    public Mono<String> StopContainer() {

        String containerId = "0cb70cfdac2e945364482531e57f1fcfd81da84d1b90aa5486e9fd878131f04e";
        return dockerWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/containers/{containerId}/stop")
                        .build(containerId))
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn("Container stoped with ID: " + containerId);

    }

    @PostMapping("/api/change-file")
    public Mono<String> changeFileInContainer() throws IOException {
        String containerId = "ed25bb1b9e60";


        GameServerSetting settings = service.loadSettings(); // 서비스 메소드는 적절한 로직으로 구현되어야 함

        // 모든 필드와 값을 가져와서 "컬럼: 값" 형식의 문자열로 변환
        String content = Arrays.stream(GameServerSetting.class.getDeclaredFields())
                .map(field -> formatField(field, settings))
                .collect(Collectors.joining("\n"));


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
        return dockerWebClient.put()
                .uri("/containers/" + containerId + "/archive?path=/server")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(tarFile)
                .retrieve()
                .bodyToMono(Void.class)  // 요청 성공시 응답 본문은 없음
                .then(restartContainer(containerId))
                .thenReturn("File updated and container restarted.");

    }

    @GetMapping("/api/get-file")
    public Mono<String> getFileFromContainer() {
        String containerId = "7f94b1a2d8e0";
        String filePathInContainer = "/server/server.properties";

        // Docker 컨테이너로부터 파일 받아오기
        return dockerWebClient.get()
                .uri("/containers/" + containerId + "/archive?path=" + filePathInContainer)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(DataBuffer.class)  // DataBuffer로 응답 바디 받기
                .flatMap(dataBuffer -> {
                    // 받아온 tar 파일을 로컬에 저장
                    Path localTarPath = Path.of("server.properties.tar");
                    try (WritableByteChannel channel = Files.newByteChannel(localTarPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                        // DataBuffer에서 데이터를 읽어 로컬 파일에 쓰기
                        channel.write(dataBuffer.asByteBuffer());
                        return Mono.just("File received and saved as " + localTarPath.toString());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }


    @GetMapping("/api/get-mcproperties")
    public Object getmcpropertiesFromContainer(){

        String tarPath = "server.properties.tar";

        try {
            String propertiesString  = extractPropertiesFromTar(tarPath);

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

    private  Mono<Void> restartContainer(String containerId) {
        return dockerWebClient.post()
                .uri("/containers/" + containerId + "/restart")
                .retrieve()
                .bodyToMono(Void.class);
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

    private String extractPropertiesFromTar(String tarFilePath) throws IOException {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(tarFilePath))) {
            tarInput.getNextTarEntry(); // server.properties 파일로 바로 이동
            ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tarInput.read(buffer)) > 0) {
                contentBuffer.write(buffer, 0, len);
            }
            return contentBuffer.toString(StandardCharsets.UTF_8.name());
        }
    }
    @PostMapping("/api/get-banlist")//특정 파일 읽어오는 용도 api
    public Mono<String> readAndConvertToJson(String containerId, String filePath) {
        String command = "cat " + filePath;
        return executeCommand(containerId, command)
                .map(content -> {
                    // 파일 내용을 JSON 객체로 변환
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    return json.toString();
                });
    }

    @GetMapping("/api/containers/{containerId}/stats")
    public Mono<String> getContainerStatsController(@PathVariable String containerId) {

        return getContainerStats(containerId);
    }

    public Mono<String> getContainerStats(String containerId) {
        return this.dockerWebClient
                .get()
                .uri("/containers/{containerId}/stats?stream=false", containerId)
                .retrieve()
                .bodyToMono(String.class);
    }

    private String formatField(Field field, GameServerSetting settings) {
        try {
            field.setAccessible(true); // private 필드에 접근할 수 있도록 설정
            String fieldName = field.getName().replace('_', '-');

            Object value = field.get(settings);
            return fieldName + ":" + value;
        } catch (IllegalAccessException e) {
            return field.getName() + ": error accessing value";
        }
    }

    public Mono<String> executeCommand(String containerId, String command) {
        // Exec 인스턴스 생성
        return dockerWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/containers/{id}/exec").build(containerId))
                .bodyValue(Map.of(
                        "AttachStdout", true,
                        "AttachStderr", true,
                        "Cmd", List.of("/bin/sh", "-c", command)
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(execCreationResponse -> {
                    String execId = (String) execCreationResponse.get("Id");

                    // Exec 시작 및 결과 수집
                    return dockerWebClient.post()
                            .uri("/exec/{id}/start", execId)
                            .bodyValue(Map.of("Detach", false, "Tty", false))
                            .retrieve()
                            .bodyToMono(String.class); // 실행 결과를 String으로 반환
                });
    }

    private void addToTar(TarArchiveOutputStream taos, Path filePath) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(filePath.toFile(), filePath.getFileName().toString());
        taos.putArchiveEntry(entry);
        Files.copy(filePath, taos);
        taos.closeArchiveEntry();
    }




    // 컨테이너 생성 응답에서 컨테이너 ID를 파싱하는 메서드 구현
    private String parseContainerId(String response) {
        // JSON 파싱 로직 구현 필요 (예: JSON 라이브러리 사용)
        // 예시 응답: {"Id":"e90e34656806","Warnings":[]}
        // 단순화된 예시 코드 (실제 구현에서는 JSON 라이브러리를 사용해야 함)
        String idStr = "\"Id\":\"";
        int startIndex = response.indexOf(idStr) + idStr.length();
        int endIndex = response.indexOf("\"", startIndex);
        return response.substring(startIndex, endIndex);
    }

    // 요청과 응답을 로깅하는 ExchangeFilterFunction
    public ExchangeFilterFunction logRequestAndResponse() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientRequest);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response: Status code " + clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientResponse);
        }));
    }
}

