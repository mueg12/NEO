package com.neo.back.docker.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.neo.back.docker.entity.DockerImage;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.entity.EdgeServer;
import com.neo.back.docker.repository.DockerImageRepository;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.EdgeServerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CloseDockerService {
    private final DockerServerRepository dockerServerRepo;
    private final DockerImageRepository dockerImageRepo;
    private final EdgeServerRepository edgeServerRepo;
    private final WebClient.Builder webClientBuilder;
    private final SaveToNasService saveToNasService;
    private WebClient dockerWebClient;
    private String imageId;

    public Mono<String> closeDockerService() {
        DockerServer dockerServer = dockerServerRepo.findByUser(null);
        if (dockerServer == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not have an open server."));
        }
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + dockerServer.getEdgeServer().getIp()+ ":2375").build();
        
        return closeContainerRequest(dockerServer)
            .switchIfEmpty(Mono.defer(() -> databaseReflection(dockerServer)));
    }



    private Mono<String> closeContainerRequest(DockerServer dockerServer) {
        return dockerWebClient.post()
            .uri("/containers/" + dockerServer.getDockerId() + "/stop")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class)
            .switchIfEmpty(Mono.defer(() -> {
                return dockerWebClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/commit")
                        .queryParam("container", dockerServer.getDockerId())
                        // .queryParam("repo", dockerServer.getServerName()) // 한글로하면 오류남
                        //.queryParam("author", author)
                        .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(commitResponse -> {
                        String imageId = parseImageId(commitResponse);
                        System.out.println(imageId); //테스트용
                        this.imageId = imageId;
                        return dockerWebClient.delete()
                            .uri("/containers/" + dockerServer.getDockerId())
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .retrieve()
                            .bodyToMono(String.class);
                    });
            }));
    }

    private Mono<String> databaseReflection(DockerServer dockerServer) {
        return dockerWebClient.get()
            .uri("/images/{imageName}/json", this.imageId)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(response -> {
                DockerImage dockerImage = new DockerImage();
                dockerImage.setServerName(dockerServer.getServerName());
                dockerImage.setUser(dockerServer.getUser());
                dockerImage.setImageId(this.imageId);
                dockerImage.setSize(parseImageSize(response));
                dockerImage.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                dockerImage.setGame(dockerServer.getGame());
                dockerImage.setSetting(dockerServer.getSetting());
                this.dockerImageRepo.save(dockerImage);

                this.dockerServerRepo.deleteById(dockerServer.getId());

                EdgeServer edgeServer = dockerServer.getEdgeServer();
                edgeServer.setMemoryUse(edgeServer.getMemoryUse() - dockerServer.getRAMCapacity());
                this.edgeServerRepo.save(edgeServer);

                saveDockerImage();
                return saveDockerImage();
            });
    }

    @SuppressWarnings("deprecation")
    private Mono<String> saveDockerImage() {
        Path dockerImagePath = Paths.get("/mnt/nas/dockerImage/test.tar");
        return dockerWebClient.get()
            .uri("/images/{imageName}/get", this.imageId)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .exchangeToMono(response -> {
                    // FileChannel을 스트림 밖에서 한 번만 열기
                    try (FileChannel channel = FileChannel.open(dockerImagePath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE)) {
                        return response.bodyToFlux(DataBuffer.class)
                                .doOnNext(dataBuffer -> {
                                    System.out.println("Received DataBuffer with capacity: " + dataBuffer.readableByteCount());
                                })
                                .flatMap(dataBuffer -> {
                                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                                    System.out.println("ByteBuffer position: " + byteBuffer.position() + ", limit: " + byteBuffer.limit());
                                    byteBuffer.flip();
                                    System.out.println("ByteBuffer position after: " + byteBuffer.position() + ", limit: " + byteBuffer.limit());
                                    if (!byteBuffer.hasRemaining()) {
                                        System.out.println("No data to write after flip.");
                                    }
                                    while (byteBuffer.hasRemaining()) {
                                        System.out.println("Writing data...");
                                        try {
                                            channel.write(byteBuffer);
                                        } catch (IOException e) {
                                            System.err.println("Error writing to file: " + e.getMessage());
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    DataBufferUtils.release(dataBuffer);  // 데이터 버퍼 해제
                                    return Mono.empty();
                                })
                                .then(Mono.just("Success"))
                                .onErrorResume(e -> Mono.just("Failed to upload file: " + e.getMessage()));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }

    private Mono<String> deleteLeftDockerImage() {
        return dockerWebClient.get()
            .uri("/images/{imageName}/get", this.imageId)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(imageInfo -> {
                

                return Mono.just("Container close & Image create success");
            });
    }

    private String parseImageId(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("Id");
    }

    private Long parseImageSize(String imageInfo) {
        JSONObject jsonObject = new JSONObject(imageInfo);
        return jsonObject.getLong("Size");
    }

}
