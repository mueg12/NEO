package com.neo.back.docker.service;

import java.util.Map;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.neo.back.docker.dto.CreateDockerDto;
import com.neo.back.docker.dto.EdgeServerInfoDto;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.EdgeServerRepository;
import com.neo.back.docker.repository.GameRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class CreateDockerService {
    private final DockerServerRepository dockerRepo;
    private final EdgeServerRepository edgeRepo;
    private final GameRepository gameRepo;
    private final SelectEdgeServerService selectEdgeServerService;
    private WebClient dockerWebClient;
    private final WebClient.Builder webClientBuilder;
    private EdgeServerInfoDto edgeServer;
    private String containerId;

    public CreateDockerService(WebClient.Builder webClientBuilder, SelectEdgeServerService selectEdgeServerService, DockerServerRepository dockerRepo, EdgeServerRepository edgeRepo, GameRepository gameRepo) {
        this.dockerRepo = dockerRepo;
        this.edgeRepo = edgeRepo;
        this.gameRepo = gameRepo;
        this.selectEdgeServerService = selectEdgeServerService;
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> createContainer(CreateDockerDto config) {

        this.edgeServer = this.selectEdgeServerService.selectingEdgeServer(config.getRamCapacity());
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + this.edgeServer.getIP()+ ":2375").filter(logRequestAndResponse()).build();

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of(
            "Image", config.getGame(),
            "ExposedPorts", Map.of(
                "25565/tcp", Map.of()
            ),
            "HostConfig", Map.of(
                "PortBindings", Map.of(
                    "25565/tcp", Collections.singletonList(
                        Map.of("HostPort", String.valueOf(edgeServer.getPortSelect()))
                    )
                ),
                "Memory", config.getRamCapacity() * 1024 * 1024 * 1024
            )
        );

        return createContainerRequest(createContainerRequest)
            .flatMap(response -> Mono.fromCallable(() -> {

                DockerServer dockerServer = new DockerServer();
                // dockerServer.setUser(null);
                dockerServer.setEdgeServer(edgeRepo.findByIp(this.edgeServer.getIP()));
                dockerServer.setPort(this.edgeServer.getPortSelect());
                dockerServer.setDockerId(this.containerId);
                dockerServer.setRAMCapacity(config.getRamCapacity());
                dockerServer.setGame(gameRepo.findByGame(config.getGame()));
                dockerServer.setSetting(gameRepo.findByGame(config.getGame()).getDefaultSetting());
                dockerRepo.save(dockerServer);

                return response;
            }));
    
    }





    // Docker 컨테이너 생성 요청하고 생성되면 실행 요청하고 응답 반환
    private Mono<String> createContainerRequest(Map<String, Object> createContainerRequest) {
        return dockerWebClient.post()
        .uri("/containers/create")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(createContainerRequest))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(createResponse -> Mono.fromCallable(() -> {
            String containerId = parseContainerId(createResponse);
            System.out.println(containerId); //테스트용
            this.containerId = containerId;
            return createResponse;
            // return dockerWebClient.post()
            //         .uri("/containers/" + containerId.substring(0,12) + "/restart")

            //         .retrieve() // 실제 요청을 보내고 응답을 받아옵니다.
            //         .bodyToMono(Void.class) // 시작 요청에 대한 본문은 필요하지 않습니다.
            //         .thenReturn("Container started with ID: " + containerId);
        })); //생성한 뒤 시작하는 코드 아직 미완.
    }

    // 컨테이너 생성 응답에서 컨테이너 ID를 파싱
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

