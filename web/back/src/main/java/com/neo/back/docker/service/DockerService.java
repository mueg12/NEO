package com.neo.back.docker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DockerService {

    private final WebClient webClient;

    public DockerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> listContainers() {
        return webClient.get()
                .uri("/containers/json")
                .retrieve()
                .bodyToMono(String.class);
    }
}
