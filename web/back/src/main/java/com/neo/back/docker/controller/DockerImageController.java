package com.neo.back.docker.controller;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class DockerImageController {

    private final WebClient dockerWebClient;

    public DockerImageController(WebClient.Builder webClientBuilder) {
        this.dockerWebClient = webClientBuilder.baseUrl("http://외부ip:2375").
                filter(logRequestAndResponse()) // 로깅 필터를 여기에 추가
                .build();
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

    private void addToTar(TarArchiveOutputStream taos, Path filePath) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(filePath.toFile(), filePath.getFileName().toString());
        taos.putArchiveEntry(entry);
        Files.copy(filePath, taos);
        taos.closeArchiveEntry();
    }

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
