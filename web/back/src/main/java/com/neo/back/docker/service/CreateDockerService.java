package com.neo.back.docker.service;

import java.util.Map;
import java.util.Collections;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

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
        DockerServer existingDocker = this.dockerRepo.findByUser(null);
        if (existingDocker != null) return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user already has an open server."));

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
            .flatMap(response -> Mono.defer(() -> {
                return databaseReflection(config);
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
            .flatMap(createResponse -> Mono.defer(() -> {
                String containerId = parseContainerId(createResponse);
                System.out.println(containerId); //테스트용
                this.containerId = containerId;
                // return createResponse;
                return dockerWebClient.post()
                        .uri("/containers/" + containerId + "/restart")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve() // 실제 요청을 보내고 응답을 받아옵니다.
                        .bodyToMono(Void.class) // 시작 요청에 대한 본문은 필요하지 않습니다.
                        .thenReturn("Container started with ID: " + containerId);
            }));
    }

    private Mono<String> databaseReflection(CreateDockerDto config) {

        DockerServer dockerServer = new DockerServer();
        dockerServer.setUser(null);
        dockerServer.setServerName(config.getServerName());
        dockerServer.setEdgeServer(this.edgeRepo.findByIp(this.edgeServer.getIP()));
        dockerServer.setPort(this.edgeServer.getPortSelect());
        dockerServer.setDockerId(this.containerId);
        dockerServer.setRAMCapacity(config.getRamCapacity());
        dockerServer.setGame(this.gameRepo.findByGame(config.getGame()));
        dockerServer.setSetting(this.gameRepo.findByGame(config.getGame()).getDefaultSetting());
        this.dockerRepo.save(dockerServer);

        return Mono.just("Container create Success");
    }

    // 컨테이너 생성 응답에서 컨테이너 ID를 파싱
    private String parseContainerId(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("Id");
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

