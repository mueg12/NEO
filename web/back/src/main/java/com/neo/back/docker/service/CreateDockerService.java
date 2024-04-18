package com.neo.back.docker.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.neo.back.docker.dto.CreateDockerDTO;

import reactor.core.publisher.Mono;

@Service
public class CreateDockerService {

    @Autowired
    private SelectEdgeServerService selectEdgeServerService;
    private WebClient dockerWebClient;
    private String edgeIp;

    public CreateDockerService(WebClient.Builder webClientBuilder, SelectEdgeServerService selectEdgeServerService) {
        this.selectEdgeServerService = selectEdgeServerService;
        this.edgeIp = selectEdgeServerService.selectingEdgeServer();
        this.dockerWebClient = webClientBuilder.baseUrl("http://" + edgeIp + ":2375").build();
    }

    public Mono<String> createContainer(CreateDockerDTO config) {

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of(
            "Image", config.getGame()//,
            // "HostConfig", Map.of(
            // "Memory", 2 * 1024 * 1024 * 1024
            // )
    );
        //if (edgeIp == null) return Mono.error("edge");//엣지서버 선택 에러

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

    // 컨테이너 생성 응답에서 컨테이너 ID를 파싱하는 메서드
    private String parseContainerId(String response) {
        // JSON 파싱 로직 구현 필요 (예: JSON 라이브러리 사용)
        // 예시 응답: {"Id":"e90e34656806","Warnings":[]}
        // 단순화된 예시 코드 (실제 구현에서는 JSON 라이브러리를 사용해야 함)
        String idStr = "\"Id\":\"";
        int startIndex = response.indexOf(idStr) + idStr.length();
        int endIndex = response.indexOf("\"", startIndex);
        return response.substring(startIndex, endIndex);
    }

}
