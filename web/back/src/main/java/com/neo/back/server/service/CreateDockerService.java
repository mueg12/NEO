package com.neo.back.server.service;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.neo.back.server.dto.CreateDockerDTO;

import reactor.core.publisher.Mono;

@Service
public class CreateDockerService {
    private WebClient webClient;

    public CreateDockerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://223.130.154.221:2375").filter(logRequestAndResponse()).build();
    }

    public Mono<String> createContainer(CreateDockerDTO config) {

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of(
            "Image", "mc1.20.4"//,
            // "HostConfig", Map.of(
            // "Memory", 2 * 1024 * 1024 * 1024
        // )
    );

        // Docker 컨테이너 생성 요청
        return this.webClient.post()
                .uri("/containers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createContainerRequest))
                .retrieve()
                .bodyToMono(String.class);
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
