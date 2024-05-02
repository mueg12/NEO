package com.neo.back.forTest;

import com.neo.back.docker.entity.DockerImage;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.entity.EdgeServer;
import com.neo.back.docker.repository.DockerImageRepository;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.EdgeServerRepository;

import com.neo.back.docker.service.SaveToNasService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ForTestController {

    private final DockerImageRepository dockerImageRepo;
    private final DockerServerRepository dockerServerRepo;
    private final EdgeServerRepository edgeServerRepo;

    @GetMapping("/test/dockerImage")
    public ResponseEntity<List<testDockerImageDto>> getTable1Data() {
        // 예시 데이터 반환
        List<DockerImage> dataList = dockerImageRepo.findAll(); // 데이터베이스에서 모든 데이터를 조회합니다.
        List<testDockerImageDto> dataDTOList = dataList.stream()
                .map(data -> new testDockerImageDto(data.getId(), data.getServerName(), data.getImageId(), data.getSize(), data.getDate(), data.getGame().getGame()))
                .collect(Collectors.toList()); // Entity 리스트를 DTO 리스트로 변환합니다.

        return ResponseEntity.ok(dataDTOList);
    }

    @GetMapping("/test/dockerServer")
    public ResponseEntity<List<testDockerServerDto>> getTable2Data() {
        // 예시 데이터 반환
        List<DockerServer> dataList = dockerServerRepo.findAll(); // 데이터베이스에서 모든 데이터를 조회합니다.
        List<testDockerServerDto> dataDTOList = dataList.stream()
                .map(data -> new testDockerServerDto(data.getId(), data.getId(), data.getServerName(), data.getEdgeServer().getEdgeServerName(), data.getPort(), data.getDockerId(), data.getRAMCapacity(), data.getGame().getGame()))
                .collect(Collectors.toList()); // Entity 리스트를 DTO 리스트로 변환합니다.

        return ResponseEntity.ok(dataDTOList);
    }

    @GetMapping("/test/edgeServer")
    public ResponseEntity<List<testEdgeServerDto>> getTable3Data() {
        // 예시 데이터 반환
        List<EdgeServer> dataList = edgeServerRepo.findAll(); // 데이터베이스에서 모든 데이터를 조회합니다.
        List<testEdgeServerDto> dataDTOList = dataList.stream()
                .map(data -> new testEdgeServerDto(data.getEdgeServerName(), data.getMemoryTotal(), data.getMemoryUse()))
                .collect(Collectors.toList()); // Entity 리스트를 DTO 리스트로 변환합니다.
        return ResponseEntity.ok(dataDTOList);
    }

    @GetMapping("/test/save")
    public Mono<ResponseEntity<String>> testSave() {
        String str = "Hello, world!";  // 저장할 문자열

        // 문자열을 바이트 배열로 인코딩
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        // ByteBuffer를 생성하고 바이트 데이터를 쓴다
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        Path rootLocation = Paths.get("/mnt/nas");
        Path dockerImagePath = rootLocation.resolve("dockerImage");
        try {
            // 파일 저장 경로 생성
            if (!Files.exists(dockerImagePath)) {
                Files.createDirectories(dockerImagePath);
            }

            Path path = dockerImagePath.resolve("test.txt");

            Files.write(path, buffer.array(), StandardOpenOption.CREATE);

            return Mono.just(ResponseEntity.ok("File uploaded successfully"));
        } catch (IOException e) {
            return Mono.just(ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage()));
        }
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> handleStorageFileNotFound(StorageException e) {
        return ResponseEntity.notFound().build();
    }

    private static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }
    }
}