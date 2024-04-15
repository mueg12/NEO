package com.neo.back.docker.controller;
import com.neo.back.docker.dto.MinecraftConfigDTO;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DockerConfigController {

    private final WebClient dockerWebClient;

    public DockerConfigController(WebClient.Builder webClientBuilder) {
        this.dockerWebClient = webClientBuilder.baseUrl("http://ip주소:2375").build();
    }

    @PostMapping("/api/create-container")
    public Mono<String> createAndStartContainer(@RequestBody MinecraftConfigDTO config) {
        // server.properties 파일 내용 생성
        String propertiesContent = "difficulty=" + config.getDifficulty() + "\n" +
                "game_mode=" + config.getGameMode();

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        Map<String, Object> createContainerRequest = new HashMap<>();
        createContainerRequest.put("Image", "minecraft-server-image");
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
                    // 컨테이너 ID 파싱 (응답 구조에 맞게 조정 필요)
                    String containerId = parseContainerId(createResponse); // parseContainerId 메서드 구현 필요

                    // 컨테이너 시작 요청
                    return dockerWebClient.post()
                            .uri("/containers/" + containerId + "/start")
                            .retrieve()
                            .bodyToMono(Void.class)
                            .thenReturn("Container created and started with ID: " + containerId);
                });
    }

    // 컨테이너 ID를 파싱하는 메서드 구현 필요
    private String parseContainerId(String response) {
        // 응답 문자열에서 컨테이너 ID를 추출하는 로직 구현
        JSONObject jsonResponse = new JSONObject(response);
        // "Id" 키로 컨테이너 ID 추출
        return jsonResponse.getString("Id");
    }
}
