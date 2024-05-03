package com.neo.back.docker.service;

import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import java.nio.file.*;

import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.neo.back.docker.dto.CreateDockerDto;
import com.neo.back.docker.dto.EdgeServerInfoDto;
import com.neo.back.docker.entity.DockerImage;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.entity.Game;
import com.neo.back.docker.middleware.DockerAPI;
import com.neo.back.docker.repository.DockerImageRepository;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.EdgeServerRepository;
import com.neo.back.docker.repository.GameRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateDockerService {
    private final DockerAPI dockerAPI;
    private final DockerServerRepository dockerRepo;
    private final EdgeServerRepository edgeRepo;
    private final DockerImageRepository imageRepo;
    private final GameRepository gameRepo;
    private final SelectEdgeServerService selectEdgeServerService;
    private WebClient dockerWebClient;
    private final WebClient.Builder webClientBuilder;
    private EdgeServerInfoDto edgeServerInfo;
    private String containerId;

    public Mono<String> createContainer(CreateDockerDto config) {
        DockerServer existingDocker = this.dockerRepo.findByUser(null);
        if (existingDocker != null) return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user already has an open server."));
        
        this.edgeServerInfo = this.selectEdgeServerService.selectingEdgeServer(config.getRamCapacity());
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + this.edgeServerInfo.getIP()+ ":2375").filter(logRequestAndResponse()).build();

        Game game = gameRepo.findByGameNameAndVersion(config.getGameName(), config.getVersion());

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of(
            "Image", game.getDockerImage(),
            "ExposedPorts", Map.of(
                "25565/tcp", Map.of()
            ),
            "HostConfig", Map.of(
                "PortBindings", Map.of(
                    "25565/tcp", Collections.singletonList(
                        Map.of("HostPort", String.valueOf(edgeServerInfo.getPortSelect()))
                    )
                ),
                "Memory", config.getRamCapacity() * 1024 * 1024 * 1024
            )
        );

        return createContainerRequest(createContainerRequest)
            .flatMap(response -> databaseReflection(config, game, null));
    
    }

    public Mono<String> recreateContainer(CreateDockerDto config) {
        DockerServer existingDocker = this.dockerRepo.findByUser(null);
        if (existingDocker != null) return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user already has an open server."));

        Optional<DockerImage> dockerImage = this.imageRepo.findById(config.getImageNum());
        this.edgeServerInfo = this.selectEdgeServerService.selectingEdgeServer(config.getRamCapacity());
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + this.edgeServerInfo.getIP()+ ":2375").filter(logRequestAndResponse()).build();

        // Docker 컨테이너 생성을 위한 JSON 객체 구성
        var createContainerRequest = Map.of(
            "Image", dockerImage.get().getImageId(),
            "ExposedPorts", Map.of(
                "25565/tcp", Map.of()
            ),
            "HostConfig", Map.of(
                "PortBindings", Map.of(
                    "25565/tcp", Collections.singletonList(
                        Map.of("HostPort", String.valueOf(edgeServerInfo.getPortSelect()))
                    )
                ),
                "Memory", config.getRamCapacity() * 1024 * 1024 * 1024
            )
        );

        config.setServerName(dockerImage.get().getServerName());

        return loadImage(dockerImage.get())
            .flatMap(response -> createContainerRequest(createContainerRequest))
            .flatMap(response -> databaseReflection(config, dockerImage.get().getGame(), dockerImage.get().getImageId()));
    
    }





    
    private Mono<String> createContainerRequest(Map<String, Object> createContainerRequest) {
        return this.dockerAPI.createContainer(createContainerRequest, this.dockerWebClient)
            .flatMap(createResponse -> Mono.defer(() -> {
                String containerId = parseContainerId(createResponse);
                this.containerId = containerId;
                return this.dockerAPI.restartContainer(containerId, this.dockerWebClient);         
            }));
    }

    private Mono<String> databaseReflection(CreateDockerDto config, Game game, String dockerImageId) {
        
        DockerServer dockerServer = new DockerServer();
        if (dockerImageId != null) {
            dockerServer.setBaseImage(dockerImageId);
        }

        dockerServer.setUser(null);
        dockerServer.setServerName(config.getServerName());
        dockerServer.setEdgeServer(this.edgeRepo.findByIp(this.edgeServerInfo.getIP()));
        dockerServer.setPort(this.edgeServerInfo.getPortSelect());
        dockerServer.setDockerId(this.containerId);
        dockerServer.setRAMCapacity(config.getRamCapacity());
        dockerServer.setGame(game);
        this.dockerRepo.save(dockerServer);

        return Mono.just("Container create Success");
    }

    private Mono<String> loadImage(DockerImage dockerImage) {
        Path filePath = Paths.get("/mnt/nas/dockerImage/" + dockerImage.getServerName() + "_" + /*dockerImage.getUser().getId() +*/ ".tar");
        FileSystemResource resource = new FileSystemResource(filePath);
        
        return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096)
            .collectList()
            .flatMap(dataBuffer -> this.dockerAPI.loadImage(dataBuffer, this.dockerWebClient));
    }

    // 컨테이너 생성 응답에서 컨테이너 ID를 파싱
    private String parseContainerId(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("Id");
    }

    // 요청과 응답을 로깅하는 ExchangeFilterFunction
    private ExchangeFilterFunction logRequestAndResponse() {
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

