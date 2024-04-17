package com.neo.back.docker.controller;
import com.neo.back.docker.dto.MinecraftConfigDTO;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@RestController
public class DockerConfigController {

    private final WebClient dockerWebClient;

    public DockerConfigController(WebClient.Builder webClientBuilder) {
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
                .bodyToMono(String.class);
                /*.flatMap(createResponse -> {
                    String containerId = parseContainerId(createResponse);
                    System.out.println(containerId);

                    return dockerWebClient.post()
                            .uri("/containers/" + containerId.substring(0,12) + "/start")

                            .retrieve() // 실제 요청을 보내고 응답을 받아옵니다.
                            .bodyToMono(Void.class) // 시작 요청에 대한 본문은 필요하지 않습니다.
                            .thenReturn("Container started with ID: " + containerId);
                });*/ //생성한 뒤 시작하는 코드 아직 미완.

    }

    @PostMapping("/api/start-container")
    public Mono<String> StartContainer() {

        String containerId = "7227c52a74c4";
        return dockerWebClient.post()
                .uri("/containers/" + containerId.substring(0, 12) + "/start")
                .retrieve() // 실제 요청을 보내고 응답을 받아옵니다.
                .bodyToMono(Void.class) // 시작 요청에 대한 본문은 필요 x
                .thenReturn("Container started with ID: " + containerId);
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
