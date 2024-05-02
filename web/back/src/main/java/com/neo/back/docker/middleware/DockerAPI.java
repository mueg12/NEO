package com.neo.back.docker.middleware;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class DockerAPI {

    public Mono<String> createContainer(Map<String, Object> requestMap, WebClient dockerWebClient) {
        return dockerWebClient.post()
            .uri("/containers/create")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestMap))
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> startContainer(String containerId, WebClient dockerWebClient) {
        return dockerWebClient.post()
            .uri("/containers/" + containerId + "/start")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class)
            .thenReturn("Container started with ID: " + containerId);
    }

    public Mono<String> restartContainer(String containerId, WebClient dockerWebClient) {
        return dockerWebClient.post()
            .uri("/containers/" + containerId + "/restart")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class)
            .thenReturn("Container started with ID: " + containerId);
    }

    public Mono<String> stopContainer(String containerId, WebClient dockerWebClient) {
        return dockerWebClient.post()
            .uri("/containers/" + containerId + "/stop")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class)
            .then(Mono.just("Stop container success"));
    }

    public Mono<String> deleteContainer(String containerId, WebClient dockerWebClient) {
        return dockerWebClient.delete()
            .uri("/containers/" + containerId)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class)
            .then(Mono.just("Delete container success"));
    }

    public Mono<String> commitContainer(String containerId, WebClient dockerWebClient) {
        return dockerWebClient.post()
            .uri(uriBuilder -> uriBuilder.path("/commit")
                .queryParam("container", containerId)
                //.queryParam("repo", dockerServer.getServerName()) // 한글로하면 오류남
                //.queryParam("author", author)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getImageInfo(String imageId, WebClient dockerWebClient) {
        return dockerWebClient.get()
            .uri("/images/{imageName}/json", imageId)
            .retrieve()
            .bodyToMono(String.class);
    }

    // public Mono<String> getImage(String imageId, WebClient dockerWebClient) {
    //     return dockerWebClient.get()
    //         .uri("/images/{imageName}/get", imageId)
    //         .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    // }

    public Mono<String> deleteImage(String imageId, WebClient dockerWebClient) {
        return dockerWebClient.delete()
            .uri("/images/{imageName}", imageId)
            .retrieve()
            .bodyToMono(String.class);
    }

}
