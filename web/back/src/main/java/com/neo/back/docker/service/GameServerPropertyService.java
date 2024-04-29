package com.neo.back.docker.service;

import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.repository.GameServerSettingRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameServerPropertyService {

    private final WebClient dockerWebClient;

    private final GameServerSettingRepository repository;

    public GameServerPropertyService(WebClient.Builder webClientBuilder, GameServerSettingRepository repository) {
        this.dockerWebClient = webClientBuilder.baseUrl("http://외부ip:2375").
                filter(logRequestAndResponse()) // 로깅 필터를 여기에 추가
                .build();
        this.repository = repository;
    }

    public GameServerSetting save(GameServerSetting property) {
        return repository.save(property);
    }

    public Mono<String> executeCommand(String containerId, String command) {
        // Exec 인스턴스 생성
        return dockerWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/containers/{id}/exec").build(containerId))
                .bodyValue(Map.of(
                        "AttachStdout", true,
                        "AttachStderr", true,
                        "Cmd", List.of("/bin/sh", "-c", command)
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(execCreationResponse -> {
                    String execId = (String) execCreationResponse.get("Id");

                    // Exec 시작 및 결과 수집
                    return dockerWebClient.post()
                            .uri("/exec/{id}/start", execId)
                            .bodyValue(Map.of("Detach", false, "Tty", false))
                            .retrieve()
                            .bodyToMono(String.class); // 실행 결과를 String으로 반환
                });
    }

    public Mono<String> ChangeFileinContainer(String containerId, byte[] tarFile) {
        return dockerWebClient.put()
                .uri("/containers/" + containerId + "/archive?path=/server")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(tarFile)
                .retrieve()
                .bodyToMono(Void.class)  // 요청 성공시 응답 본문은 없음
                .then(restartContainer(containerId))
                .thenReturn("File updated and container restarted.");
    }

    public GameServerSetting loadSettings() {
        // 처음 데이터 반환하게 함.
        // 더미 데이터 일단은 넣어두고 테스트
        return repository.findById(1L).orElseThrow(() -> new RuntimeException("Settings not found"));
    }

    private  Mono<Void> restartContainer(String containerId) {
        return dockerWebClient.post()
                .uri("/containers/" + containerId + "/restart")
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<String> getContainerStats(String containerId) {
        return this.dockerWebClient
                .get()
                .uri("/containers/{containerId}/stats?stream=false", containerId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public String getString(GameServerSetting settings) {
        String content = Arrays.stream(GameServerSetting.class.getDeclaredFields())
                .map(field -> formatField(field, settings))
                .collect(Collectors.joining("\n"));
        return content;
    }
    public  Mono<String> getDockerContainerFile(String containerId, String filePathInContainer) {
        return dockerWebClient.get()
                .uri("/containers/" + containerId + "/archive?path=" + filePathInContainer)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(DataBuffer.class)  // DataBuffer로 응답 바디 받기
                .flatMap(dataBuffer -> {
                    // 받아온 tar 파일을 로컬에 저장
                    Path localTarPath = Path.of("server.properties.tar");
                    try (WritableByteChannel channel = Files.newByteChannel(localTarPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                        // DataBuffer에서 데이터를 읽어 로컬 파일에 쓰기
                        channel.write(dataBuffer.asByteBuffer());
                        return Mono.just("File received and saved as " + localTarPath.toString());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }
    public String extractPropertiesFromTar(String tarFilePath) throws IOException {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(tarFilePath))) {
            tarInput.getNextTarEntry(); // server.properties 파일로 바로 이동
            ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tarInput.read(buffer)) > 0) {
                contentBuffer.write(buffer, 0, len);
            }
            return contentBuffer.toString(StandardCharsets.UTF_8.name());
        }
    }

    private String formatField(Field field, GameServerSetting settings) {
        try {
            field.setAccessible(true); // private 필드에 접근할 수 있도록 설정
            String fieldName = field.getName().replace('_', '-');

            Object value = field.get(settings);
            return fieldName + ":" + value;
        } catch (IllegalAccessException e) {
            return field.getName() + ": error accessing value";
        }
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
