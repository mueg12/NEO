package com.neo.back.docker.controller;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
@RestController
public class DockerController {

    private final WebClient dockerWebClient;

    public DockerController(WebClient.Builder webClientBuilder) {
        this.dockerWebClient = webClientBuilder.baseUrl("http://외부ip:2375").
                filter(logRequestAndResponse()) // 로깅 필터를 여기에 추가
                .build();
    }

    @PostMapping("/api/create-container")
    public Mono<String> createAndStartContainer(@RequestBody MinecraftConfigDTO config) {
        // server.properties 파일 내용 생성
        String propertiesContent = "difficulty=" + config.getDifficulty() + "\n" +
                "game_mode=" + config.getGameMode();

        System.out.println(propertiesContent);

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of("Image", "hello-world");
        // 여기에 추가 설정
        System.out.println("hello");
        // Docker 컨테이너 생성 요청
        return dockerWebClient.post()
                .uri("/containers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createContainerRequest))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(createResponse -> {
                    String containerId = parseContainerId(createResponse);
                    System.out.println(containerId);

                    return dockerWebClient.post()
                            .uri("/containers/" + containerId.substring(0,12) + "/restart")

                            .retrieve() // 실제 요청을 보내고 응답을 받아옵니다.
                            .bodyToMono(Void.class) // 시작 요청에 대한 본문은 필요하지 않습니다.
                            .thenReturn("Container started with ID: " + containerId);
                }); //생성한 뒤 시작하는 코드 아직 미완.

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
    public Mono<String> changeFileAndBuildImage() throws IOException {
        Path dockerfilePath = Path.of("Dockerfile");
        Path helloFilePath = Path.of("hello.txt");

        // Dockerfile 및 hello.txt 파일의 내용을 변경하거나 준비합니다.
        Files.writeString(dockerfilePath, "FROM alpine\nWORKDIR /app\nCOPY hello.txt /app\nCMD [\"cat\", \"/app/hello.txt\"]");
        Files.writeString(helloFilePath, "Hello, Docker! Modified!");

        // context.tar 파일 생성
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream taos = new TarArchiveOutputStream(new BufferedOutputStream(baos))) {
            addToTar(taos, dockerfilePath);
            addToTar(taos, helloFilePath);
            taos.finish();
        }

        byte[] tarContent = baos.toByteArray();

        // Docker 이미지 빌드 요청
        return dockerWebClient.post()
                .uri("/build?t=my-custom-image")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(tarContent)
                .retrieve()
                .bodyToMono(String.class);
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

