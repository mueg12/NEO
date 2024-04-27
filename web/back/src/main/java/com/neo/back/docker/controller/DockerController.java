package com.neo.back.docker.controller;

import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.service.GameServerPropertyService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
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
        String containerId = "your_container_id";


        GameServerSetting settings = service.loadSettings(); // 서비스 메소드는 적절한 로직으로 구현되어야 함

        // 모든 필드와 값을 가져와서 "컬럼: 값" 형식의 문자열로 변환
        String content = Arrays.stream(GameServerSetting.class.getDeclaredFields())
                .map(field -> formatField(field, settings))
                .collect(Collectors.joining("\n"));


        // 파일에 저장
        Path path = Path.of("server.properties");
        Files.writeString(path, content);

        // 파일 내용을 tar 파일로 압축 (예제에서는 생략)
        byte[] tarFile = createTarContent(content);


        // Docker API를 통해 파일을 컨테이너에 복사
        return dockerWebClient.put()
                .uri("/containers/" + containerId + "/archive?path=/app/")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(tarFile)
                .retrieve()
                .bodyToMono(Void.class)  // 요청 성공시 응답 본문은 없음
                .then(restartContainer(containerId))
                .thenReturn("File updated and container restarted.");

    }

    private byte[] createTarContent(byte[] fileContent) {
        // 여기에서 tar 파일 생성 로직을 구현해야 함. 실제 코드는 해당 로직에 따라 달라짐
        return fileContent;  // 예제 코드로 실제로는 tar로 변환해야 함
    }

    private  Mono<Void> restartContainer(String containerId) {
        return dockerWebClient.post()
                .uri("/containers/" + containerId + "/restart")
                .retrieve()
                .bodyToMono(Void.class);
    }

    private File createTarFile(Path filePath) throws IOException {
        // 파일을 tar로 압축하는 로직 구현 필요
        // 이 예제에서는 단순화를 위해 직접 구현하지 않음
        return filePath.toFile();  // 실제 사용시에는 tar 압축 로직을 구현해야 함
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
            Object value = field.get(settings);
            return field.getName() + ": " + value;
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

